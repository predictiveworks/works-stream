package de.kp.works.stream.things

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

import de.kp.works.stream.ssl.SslOptions
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttConnectOptions}

import java.util.Properties
import javax.net.ssl.SSLSocketFactory
import scala.collection.JavaConverters._

class ThingsOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  private val THINGS_BOARD_TOPIC:String = "v1/gateway/attributes"

  def getBrokerUrl:String = {

    if (!settings.contains(ThingsNames.BROKER_URL))
      throw new Exception(s"[ThingsOptions] No `broker.url` provided. Please specify in options.")

    settings(ThingsNames.BROKER_URL)

  }

  def getClientId:String = {

    if (!settings.contains(ThingsNames.CLIENT_ID)) {
      MqttClient.generateClientId()

    } else
      settings(ThingsNames.CLIENT_ID)

  }

  def getMqttOptions:MqttConnectOptions = {

    val mqttConnectOptions = new MqttConnectOptions

    /* User authentication */

    val username: Option[String] =
      settings.get(ThingsNames.USERNAME)

    val password: Option[String] =
      settings.get(ThingsNames.PASSWORD)

    (username, password) match {

      case (Some(u: String), Some(p: String)) =>
        mqttConnectOptions.setUserName(u)
        mqttConnectOptions.setPassword(p.toCharArray)

      case _ =>

    }

    /* Connection timeout */

    val connectionTimeout: Int =
      settings.getOrElse(ThingsNames.TIMEOUT,
        MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT.toString).toInt

    mqttConnectOptions.setConnectionTimeout(connectionTimeout)

    /* Keep alive interval */

    val keepAlive: Int =
      settings.getOrElse(ThingsNames.KEEP_ALIVE,
        MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT.toString).toInt

    mqttConnectOptions.setKeepAliveInterval(keepAlive)

    /* Mqtt version */

    val mqttVersion: Int = {
      val version = settings.get(ThingsNames.VERSION)
      version match {
        case Some(v) =>
          v match {
            case "3.1" =>
              MqttConnectOptions.MQTT_VERSION_3_1
            case "3.1.1" =>
              MqttConnectOptions.MQTT_VERSION_3_1_1
            case _ =>
              MqttConnectOptions.MQTT_VERSION_DEFAULT
          }
        case _ => MqttConnectOptions.MQTT_VERSION_DEFAULT
      }
    }

    mqttConnectOptions.setMqttVersion(mqttVersion)

    /* Clean session */

    val cleanSession: Boolean = settings
      .getOrElse(ThingsNames.CLEAN_SESSION, "false").toBoolean

    mqttConnectOptions.setCleanSession(cleanSession)

    /* Auto reconnect */

    val autoReconnect: Boolean = settings
      .getOrElse(ThingsNames.AUTO_RECONNECT, "false").toBoolean

    mqttConnectOptions.setAutomaticReconnect(autoReconnect)

    /* MAX INFLIGHT */

    val maxInflight: Int = settings
      .getOrElse(ThingsNames.MAX_INFLIGHT, "60").toInt

    mqttConnectOptions.setMaxInflight(maxInflight)

    /* Check whether SSL settings are provided */

    if (!isSsl) return mqttConnectOptions

    mqttConnectOptions.setSocketFactory(getSslSocketFactory)
    mqttConnectOptions

  }

  def getQos:Int = {
    settings.getOrElse(ThingsNames.QOS, "1").toInt
  }

  private def isSsl:Boolean = {

    var ssl:Boolean = false
    settings.keySet.foreach(key => {
      if (key.startsWith("ssl.")) ssl = true
    })

    ssl

  }

  def getNumThreads:Int = {
    settings.getOrElse(ThingsNames.NUM_THREADS, "1").toInt
  }

  private def getSslSocketFactory:SSLSocketFactory = {

    val tlsVersion = settings
      .getOrElse(ThingsNames.SSL_PROTOCOL, "TLS")

    /*
     * The keystore file must be defined
     */
    val keyStoreFile = settings
      .getOrElse(ThingsNames.SSL_KEYSTORE_FILE,
        throw new Exception(s"No keystore file specified."))
    /*
     * - JKS      Java KeyStore
     * - JCEKS    Java Cryptography Extension KeyStore
     * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
     * - BKS      Bouncy Castle KeyStore
     * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
     */
    val keyStoreType = settings
      .getOrElse(ThingsNames.SSL_KEYSTORE_TYPE, "JKS")

    val keyStorePass = settings
      .getOrElse(ThingsNames.SSL_KEYSTORE_PASS, "")

    val keyStoreAlgo = settings
      .getOrElse(ThingsNames.SSL_KEYSTORE_ALGO, "SunX509")

    val trustStoreFile = settings.get(ThingsNames.SSL_TRUSTSTORE_FILE)
    /*
     * - JKS      Java KeyStore
     * - JCEKS    Java Cryptography Extension KeyStore
     * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
     * - BKS      Bouncy Castle KeyStore
     * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
     */
    val trustStoreType = settings
      .getOrElse(ThingsNames.SSL_TRUSTSTORE_TYPE, "JKS")

    val trustStorePass = settings
      .getOrElse(ThingsNames.SSL_TRUSTSTORE_PASS, "")

    val trustStoreAlgo = settings
      .getOrElse(ThingsNames.SSL_TRUSTSTORE_ALGO, "SunX509")

    val cipherSuites = settings
      .getOrElse(ThingsNames.SSL_CIPHER_SUITES, "")
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

    sslOptions.getSslSocketFactory

  }

  def getTopic:String = THINGS_BOARD_TOPIC
}
