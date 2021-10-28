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

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy}
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.google.gson._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.Try

abstract class BaseActor(options:FiwareOptions) extends Actor with ActorLogging {
  /**
   * The header positions that hold service and
   * service path information
   */
  val SERVICE_HEADER = 4
  val SERVICE_PATH_HEADER = 5
  /**
   * The actor system is implicitly accompanied by a materializer,
   * and this materializer is required to retrieve the ByteString
   */
  implicit val system: ActorSystem = context.system
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val timeout: Timeout = {
    val value = options.getActorTimeout
    Timeout(value.seconds)
  }
  /**
   * Parameters to control the handling of failed child actors:
   * it is the number of retries within a certain time window.
   *
   * The supervisor strategy restarts a child up to 10 restarts
   * per minute. The child actor is stopped if the restart count
   * exceeds maxNrOfRetries during the withinTimeRange duration.
   */
  protected val maxRetries: Int = options.getActorMaxRetries
  protected val timeRange: FiniteDuration = {
    val value = options.getActorTimeRange
    value.minute
  }
  /**
   * Child actors are defined leveraging a RoundRobin pool with a
   * dynamic resizer. The boundaries of the resizer are defined
   * below
   */
  protected val lower: Int = options.getActorLowerBound
  protected val upper: Int = options.getActorUpperBound
  /**
   * The number of instances for the RoundRobin pool
   */
  protected val instances: Int = options.getActorInstances
  /**
   * Each actor is the supervisor of its children, and as such each
   * actor defines fault handling supervisor strategy. This strategy
   * cannot be changed afterwards as it is an integral part of the
   * actor system’s structure.
   */
  override val supervisorStrategy: OneForOneStrategy =
    /*
     * The implemented supervisor strategy treats each child separately
     * (one-for-one). Alternatives are, e.g. all-for-one.
     *
     */
    OneForOneStrategy(maxNrOfRetries = maxRetries, withinTimeRange = timeRange) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
    }
  /**
   * Method to unpack the (incoming) Orion Broker notification
   * request and to transform into an internal JSON format for
   * further processing.
   */
  def toFiwareEvent(request:HttpRequest):FiwareEvent = {

    /** HEADERS **/

    val headers = request.headers

    val service = headers(SERVICE_HEADER).value()
    val servicePath = headers(SERVICE_PATH_HEADER).value()

    /** BODY **/

    /* Extract body as String from request entity */

    val source = request.entity.dataBytes
    val future = source.runFold(ByteString(""))(_ ++ _)
    /*
     * We do not expect to retrieve large messages
     * and accept a blocking wait
     */
    val bytes = Await.result(future, timeout.duration)
    val body = bytes.decodeString("UTF-8")

    /* We expect that the Orion Context Broker sends a JSON object */
    val payload = JsonParser.parseString(body).getAsJsonObject
    FiwareEvent(service, servicePath, payload)

  }

}
/**
 * The [FiwareActor] is responsible for executing HTTP based
 * notification requests from the Orion Broker. Each notification
 * is checked whether it refers to a registered subscriptions,
 * and, if this is the case, further processing is delegated to
 * the output handler
 */
class FiwareActor(options:FiwareOptions, handler:FiwareHandler) extends BaseActor(options) {

  import FiwareActor._

  override def receive: Receive = {

    case request: HttpRequest =>
      sender ! Response(Try({
        execute(request)
      })
        .recover {
          case e: Exception =>
            throw new Exception(e.getMessage)
        })

  }
  /**
   * Method to transform an Orion Broker notification
   * message into an internal format and to delegate
   * further processing to the provided callback
   */
  private def execute(request: HttpRequest):Unit = {
    /*
     * Convert Http request from Orion Broker
     * into internal notification format
     */
    val fiwareEvent = toFiwareEvent(request)
    handler.sendFiwareEvent(fiwareEvent)

  }
}

object FiwareActor {

  val ACTOR_NAME = "FiwareActor"

  case class Response(status: Try[_])

}