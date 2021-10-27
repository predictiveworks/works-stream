package de.kp.works.stream.mqtt.hivemq

/*
 * Copyright (c) 2020 - 2021 Dr. Krusche & Partner PartG. All rights reserved.
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

import de.kp.works.stream.mqtt.MqttNames
import de.kp.works.stream.ssl.SslOptions

import java.util.Properties
import scala.collection.JavaConverters._

class HiveOptions(properties: Properties) {

  private val settings:Map[String,String] = properties.asScala.toMap

  def isSsl:Boolean = {

    var ssl:Boolean = false
    settings.keySet.foreach(key => {
      if (key.startsWith("ssl.")) ssl = true
    })

    ssl

  }

  /* Connection */

  def getHostAndPort:(String, Int) = {

    val host =
      settings.getOrElse(MqttNames.HOST, "")

    if (host.isEmpty)
      throw new Exception(s"[HiveOptions] No `host` provided. Please specify in options.")

    val port =
      settings.getOrElse(MqttNames.PORT, "")

    if (port.isEmpty)
      throw new Exception(s"[HiveOptions] No `port` provided. Please specify in options.")

    (host, port.toInt)

  }

  /* Keep alive interval */

  def getKeepAlive: Int =
    settings.getOrElse(MqttNames.KEEP_ALIVE, 60.toString).toInt

  def getQoS:Int = {
    settings.getOrElse(MqttNames.QOS, "1").toInt
  }

  def getSslOptions:SslOptions = {

    val tlsVersion = settings
      .getOrElse(MqttNames.SSL_PROTOCOL, "TLS")

    /*
     * The keystore file must be defined
     */
    val keyStoreFile = settings
      .getOrElse(MqttNames.SSL_KEYSTORE_FILE,
        throw new Exception(s"No keystore file specified."))
    /*
     * - JKS      Java KeyStore
     * - JCEKS    Java Cryptography Extension KeyStore
     * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
     * - BKS      Bouncy Castle KeyStore
     * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
     */
    val keyStoreType = settings
      .getOrElse(MqttNames.SSL_KEYSTORE_TYPE, "JKS")

    val keyStorePass = settings
      .getOrElse(MqttNames.SSL_KEYSTORE_PASS, "")

    val keyStoreAlgo = settings
      .getOrElse(MqttNames.SSL_KEYSTORE_ALGO, "SunX509")

    val trustStoreFile = settings.get(MqttNames.SSL_TRUSTSTORE_FILE)
    /*
     * - JKS      Java KeyStore
     * - JCEKS    Java Cryptography Extension KeyStore
     * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
     * - BKS      Bouncy Castle KeyStore
     * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
     */
    val trustStoreType = settings
      .getOrElse(MqttNames.SSL_TRUSTSTORE_TYPE, "JKS")

    val trustStorePass = settings
      .getOrElse(MqttNames.SSL_TRUSTSTORE_PASS, "")

    val trustStoreAlgo = settings
      .getOrElse(MqttNames.SSL_TRUSTSTORE_ALGO, "SunX509")

    val cipherSuites = settings
      .getOrElse(MqttNames.SSL_CIPHER_SUITES, "")
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

    sslOptions

  }

  def getTopics:Array[String] = {

    if (!settings.contains(MqttNames.TOPICS)) {
      throw new Exception(s"[HiveOptions] No `topics` provided. Please specify in options.")
    }
    /*
     * Comma-separated list of topics
     */
    settings(MqttNames.TOPICS)
      .split(",").map(_.trim)

  }

  /* User authentication */

  def getUserAndPass:(String, String) = {

    val username =
      settings.getOrElse(MqttNames.USERNAME, "")

    val password =
      settings.getOrElse(MqttNames.PASSWORD, "")

    (username, password)

  }

  def getVersion: Int = {
    val version = settings.get(MqttNames.VERSION)
    version match {
      case Some(v) =>
        v match {
          case "3.1.1" =>
            3
          case "5" =>
            5
          case _ =>
            3
        }
      case _ => 3
    }
  }

}
