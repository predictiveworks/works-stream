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

import org.eclipse.paho.client.mqttv3
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Date

class ThingsMonitor(options:ThingsOptions, handler:ThingsHandler) {

  private var client:mqttv3.MqttClient = _

  def start(): Unit = {

    val UTF8 = Charset.forName("UTF-8")
    val MD5 = MessageDigest.getInstance("MD5")

    /* 						MESSAGE PERSISTENCE
     *
     * Since we don’t want to persist the state of pending
     * QoS messages and the persistent session, we are just
     * using a in-memory persistence. A file-based persistence
     * is used by default.
     */
    val persistence = new MemoryPersistence()
    /*
      * Initializing Mqtt Client specifying brokerUrl, clientId
      * and MqttClientPersistence
      */
    val clientId = options.getClientId
    val brokerUrl = options.getBrokerUrl

    client = new mqttv3.MqttClient(brokerUrl, clientId, persistence)

    /* Initialize mqtt parameters */
    val mqttOptions = options.getMqttOptions

    /*
     * Callback automatically triggers as and when new message
     * arrives on specified topic
     */
    val callback: mqttv3.MqttCallback = new mqttv3.MqttCallback() {

      override def messageArrived(topic: String, message: mqttv3.MqttMessage) {

        val payload = message.getPayload
        if (payload == null) {

          /* Do nothing */

        } else {

          /* Timestamp when the message arrives */
          val timestamp = new Date().getTime
          val seconds = timestamp / 1000

          val payload = message.getPayload
          if (payload == null) {

            /* Do nothing */

          } else {

            val qos = message.getQos

            val duplicate = message.isDuplicate
            val retained = message.isRetained

            /* Serialize plain byte message */
            val json = new String(payload, UTF8)

            /* Extract metadata from topic and
             * serialized payload
             */
            val serialized = Seq(topic, json).mkString("|")
            val digest = MD5.digest(serialized.getBytes).toString

            val tokens = topic.split("\\/").toList

            val context = MD5.digest(tokens.init.mkString("/").getBytes).toString
            val dimension = tokens.last

            val thingsEvent = new ThingsEvent(timestamp, seconds, topic, qos, duplicate, retained, payload, digest, json, context, dimension)
            handler.sendThingsEvent(thingsEvent.toJson)

          }
        }
      }

      override def deliveryComplete(token: mqttv3.IMqttDeliveryToken) {}

      override def connectionLost(cause: Throwable) {
        restart(cause)
      }

    }

    /*
     * Set up callback for MqttClient. This needs to happen before
     * connecting or subscribing, otherwise messages may be lost
     */
    client.setCallback(callback)

    /* Connect to MqttBroker */
    client.connect(mqttOptions)

    /* Subscribe to ThingsBoard topic */
    val qos = options.getQos
    val topic = options.getTopic

    client.subscribe(topic, qos)

  }

  def restart(t:Throwable): Unit = {

    val now = new java.util.Date().toString
    println(s"[MqttMonitor] $now - Restart due to: ${t.getLocalizedMessage}")

    start()

  }

}


