package de.kp.works.stream.fiware
/*
 * Copyright (c) 2019 - 2021 Dr. Krusche & Partner PartG. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author Stefan Krusche, Dr. Krusche & Partner PartG
 *
 */

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import de.kp.works.stream.ssl.SslOptions

import scala.collection.JavaConversions._
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class FiwareMonitor(options:FiwareOptions, handler:FiwareHandler) {

  private var server:Option[Future[Http.ServerBinding]] = None
  /**
   * Akka 2.6 provides a default materializer out of the box, i.e., for Scala
   * an implicit materializer is provided if there is an implicit ActorSystem
   * available. This avoids leaking materializers and simplifies most stream
   * use cases somewhat.
   */
  implicit val system: ActorSystem = ActorSystem("fiware-monitor")
  implicit lazy val context: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  /**
   * Common timeout for all Akka connection
   */
  implicit val timeout: Timeout = Timeout(5.seconds)

  def start(): Unit = {
    /*
     * STEP #1: Build HTTP(s) server based on Akka,
     * and implicitly start server.
     */
    val routes = buildRoute
    val binding = options.getBinding

    val host = binding.host
    val port = binding.port

    server =
      if (!options.isServerSsl)
        Some(Http().bindAndHandle(routes , host, port))

      else {

        val sslOptions = options.getServerSslOptions
        if (sslOptions.isEmpty)
          throw new Exception("No SSL configuration provided.")

        val context = buildServerConnectionContext(sslOptions.get)
        Some(Http().bindAndHandle(routes, host, port, connectionContext = context))

      }
    /*
     * STEP #2: After start processing sends
     * subscriptions to the configured Fiware
     * server to listen to notifications
     */
    onStart()

  }

  def stop():Unit = {

    if (server.isEmpty)
      throw new Exception("Fiware server was not launched.")

    server.get
      /*
       * Trigger unbinding from port
       */
      .flatMap(_.unbind())
      /*
       * Shut down application
       */
      .onComplete(_ => {
        system.terminate()
      })

  }

  private def buildRoute:Route = ???
  /**
   * In [[akka-http]] version 10.0.x is no difference between
   * client and service side HTTPS context
   */
  private def buildClientConnectionContext(sslOptions:SslOptions): HttpsConnectionContext = {
    ConnectionContext.https(sslContext = sslOptions.getSslContext)
  }

  private def buildServerConnectionContext(sslOptions:SslOptions): HttpsConnectionContext = {
    ConnectionContext.https(sslContext = sslOptions.getSslContext)
  }
  /**
   * This method sends configured Fiware subscriptions
   * to the Fiware Context Broker
   */
  private def onStart():Unit = {
    /*
     * The current implementation expects that the Fiware
     * subscriptions are provided as configuration property
     */
    val subscriptions = options.getSubscriptions
    subscriptions.foreach(subscription => {

      try {

        val future = subscribe(subscription.toString, system)
        val response = Await.result(future, timeout.duration)

        val sid = getSubscriptionId(response)
        FiwareRegistry.register(sid, subscription.toString)

      } catch {
        case _:Throwable =>
          /*
           * The current implementation of the Fiware
           * support is an optimistic approach that
           * focuses on those subscriptions that were
           * successfully registered.
           */
          println("[ERROR] ------------------------------------------------")
          println("[ERROR] Registration of subscription failed:")
          println(s"$subscription")
          println("[ERROR] ------------------------------------------------")
      }

    })

  }
  /**
   * This method creates a single Akka based Http(s) request
   * to send the provided subscription to the Context Broker.
   * <p>
   * Each subscription must contain the endpoint of the Fiware
   * notification server.
   */
  private def subscribe(subscription:String, system:ActorSystem):Future[HttpResponse] = {

    try {

      val entity = subscription.getBytes("UTF-8")
      /*
       * Build request: A subscription is registered with a POST request
       * to /v2/subscriptions
       */
      val brokerUrl = options.getBrokerUrl
      val endpoint = s"$brokerUrl/v2/subscriptions"

      val headers = List(`Content-Type`(`text/plain(UTF-8)`))
      val request = HttpRequest(
        HttpMethods.POST, endpoint,entity = HttpEntity(`application/json`, entity))
        .withHeaders(headers)

      val response: Future[HttpResponse] = {

        if (!options.isFiwareSsl)
        /*
         * The request protocol in the broker url must be
         * specified as 'http://'
         */
          Http(system).singleRequest(request)

        else {
          /*
           * The request protocol in the broker url must be
           * specified as 'https://'. In this case, an SSL
           * security context must be specified
           */
          val sslOptions = options.getFiwareSslOptions
          if (sslOptions.isEmpty)
            throw new Exception("No SSL configuration provided.")

          val context = buildClientConnectionContext(sslOptions.get)
          Http(system).singleRequest(request = request, connectionContext = context)

        }
      }
      response

    } catch {
      case _:Throwable => null
    }

  }

  /**
   * This method validates whether the response code of
   * the Fiware Context Broker response is 201 (Created)
   * and then the subscription ID is extracted from the
   * provided 'Location' header
   */
  private def getSubscriptionId(response:HttpResponse):String = {

    var sid:Option[String] = None

    val statusCode = response._1
    if (statusCode == StatusCodes.Created) {
      /*
       * The Orion Context Broker responds with a 201 Created response
       * code; the subscription identifier is provided through the
       * Location Header
       */
      val headers = response._2

      headers.foreach(header => {
        /* Akka HTTP requires header in lower cases */
        if (header.is("location")) {
          /*
           * Location: /v2/subscriptions/57458eb60962ef754e7c0998
           *
           * Subscription ID: a 24 digit hexadecimal number used
           * for updating and cancelling the subscription. It is
           * used to identify the notifications that refer to this
           * subscription
           */
          sid = Some(header.value().replace("/v2/subscriptions/",""))
        }

      })
    }

    if (sid.isEmpty)
      throw new Exception("Fiware Context Broker did not respond with a subscription response.")

    sid.get

  }

}


