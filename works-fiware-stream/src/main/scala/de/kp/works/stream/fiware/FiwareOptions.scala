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

import com.google.gson.{JsonArray, JsonParser}
import de.kp.works.stream.ssl.SslOptions

import java.util.Properties
import scala.collection.JavaConverters._

case class HttpBinding(host:String, port:Int)

class FiwareOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  /**
   * ACTOR SPECIFIC CONFIGURATIONS
   */

  /**
   * Timeout in seconds
   */
  def getActorTimeout:Int = 5
  /**
   * Parameters to control the handling of failed child actors:
   * it is the number of retries within a certain time window.
   *
   * The supervisor strategy restarts a child up to 10 restarts
   * per minute. The child actor is stopped if the restart count
   * exceeds maxRetries during the withinTimeRange duration.
   *
   */
  def getActorMaxRetries:Int = 10
  /**
   * Time range in minutes
   */
  def getActorTimeRange:Int = 1
  /**
   * Child actors are defined leveraging a RoundRobin pool with a
   * dynamic resizer. The boundaries of the resizer are defined
   * below
   */
  def getActorLowerBound = 2
  def getActorUpperBound = 100

  /**
   * The number of instances for the RoundRobin pool
   */
  def getActorInstances = 50

  /**
   * EXTERNAL CONFIGURATION
   */
  def getBinding:HttpBinding = {

    val host = settings(FiwareNames.SERVER_HOST)
    val port = settings(FiwareNames.SERVER_PORT).toInt

    HttpBinding(host, port)

  }

  def getBrokerUrl:String =
    settings(FiwareNames.BROKER_URL)

  /**
   * This method returns `true` if at least
   * one SSL configuration key is provided
   */
  def isFiwareSsl:Boolean = {

    var fiwareSsl:Boolean = false

    settings.keys.foreach(key => {
      if (key.startsWith("fiware.broker.ssl."))
        fiwareSsl = true
    })

    fiwareSsl

  }

  def getFiwareSslOptions:Option[SslOptions] = {

    try {
      val tlsVersion = settings
        .getOrElse(FiwareNames.BROKER_SSL_PROTOCOL, "TLS")

      /*
       * The keystore file must be defined
       */
      val keyStoreFile = settings
        .getOrElse(FiwareNames.BROKER_SSL_KEYSTORE_FILE,
          throw new Exception(s"No keystore file specified."))
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val keyStoreType = settings
        .getOrElse(FiwareNames.BROKER_SSL_KEYSTORE_TYPE, "JKS")

      val keyStorePass = settings
        .getOrElse(FiwareNames.BROKER_SSL_KEYSTORE_PASS, "")

      val keyStoreAlgo = settings
        .getOrElse(FiwareNames.BROKER_SSL_KEYSTORE_ALGO, "SunX509")

      val trustStoreFile = settings.get(FiwareNames.BROKER_SSL_TRUSTSTORE_FILE)
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val trustStoreType = settings
        .getOrElse(FiwareNames.BROKER_SSL_TRUSTSTORE_TYPE, "JKS")

      val trustStorePass = settings
        .getOrElse(FiwareNames.BROKER_SSL_TRUSTSTORE_PASS, "")

      val trustStoreAlgo = settings
        .getOrElse(FiwareNames.BROKER_SSL_TRUSTSTORE_ALGO, "SunX509")

      val cipherSuites = settings
        .getOrElse(FiwareNames.BROKER_SSL_CIPHER_SUITES, "")
        .split(",").map(_.trim).toList

      val sslOptions = if (trustStoreFile.isEmpty) {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          cipherSuites
        )
      }
      else {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          trustStoreFile.get,
          trustStoreType,
          trustStorePass,
          trustStoreAlgo,
          cipherSuites)

      }

      Some(sslOptions)

    } catch {
      case _:Throwable => None
    }

  }

  def getNumThreads:Int =
    settings.getOrElse(FiwareNames.NUM_THREADS, "1").toInt

  /**
   * This method returns `true` if at least
   * one SSL configuration key is provided
   */
  def isServerSsl:Boolean = {

    var serverSsl:Boolean = false

    settings.keys.foreach(key => {
      if (key.startsWith("fiware.server.ssl."))
        serverSsl = true
    })

    serverSsl

  }

  def getServerSslOptions:Option[SslOptions] = {

    try {
      val tlsVersion = settings
        .getOrElse(FiwareNames.SERVER_SSL_PROTOCOL, "TLS")

      /*
       * The keystore file must be defined
       */
      val keyStoreFile = settings
        .getOrElse(FiwareNames.SERVER_SSL_KEYSTORE_FILE,
          throw new Exception(s"No keystore file specified."))
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val keyStoreType = settings
        .getOrElse(FiwareNames.SERVER_SSL_KEYSTORE_TYPE, "JKS")

      val keyStorePass = settings
        .getOrElse(FiwareNames.SERVER_SSL_KEYSTORE_PASS, "")

      val keyStoreAlgo = settings
        .getOrElse(FiwareNames.SERVER_SSL_KEYSTORE_ALGO, "SunX509")

      val trustStoreFile = settings.get(FiwareNames.SERVER_SSL_TRUSTSTORE_FILE)
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val trustStoreType = settings
        .getOrElse(FiwareNames.SERVER_SSL_TRUSTSTORE_TYPE, "JKS")

      val trustStorePass = settings
        .getOrElse(FiwareNames.SERVER_SSL_TRUSTSTORE_PASS, "")

      val trustStoreAlgo = settings
        .getOrElse(FiwareNames.SERVER_SSL_TRUSTSTORE_ALGO, "SunX509")

      val cipherSuites = settings
        .getOrElse(FiwareNames.SERVER_SSL_CIPHER_SUITES, "")
        .split(",").map(_.trim).toList

      val sslOptions = if (trustStoreFile.isEmpty) {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          cipherSuites
        )
      }
      else {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          trustStoreFile.get,
          trustStoreType,
          trustStorePass,
          trustStoreAlgo,
          cipherSuites)

      }

      Some(sslOptions)

    } catch {
      case _:Throwable => None
    }

  }

  def getSubscriptions:JsonArray = {

    if (!settings.contains(FiwareNames.BROKER_SUBSCRIPTIONS))
      throw new Exception("No Fiware subscriptions provided")

    val subscriptions = settings(FiwareNames.BROKER_SUBSCRIPTIONS)
    JsonParser.parseString(subscriptions).getAsJsonArray

  }

}
