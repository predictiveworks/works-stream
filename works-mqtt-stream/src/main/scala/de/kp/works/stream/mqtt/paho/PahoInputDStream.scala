package de.kp.works.stream.mqtt.paho
/*
 * Copyright (c) 2020 Dr. Krusche & Partner PartG. All rights reserved.
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

import de.kp.works.stream.mqtt.MqttEvent
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.{Date, Properties}


/**
 * MQTT v3.1 & MQTT v3.1.1
 * 
 * 
 * This input stream uses the Eclipse Paho MqttClient (http://www.eclipse.org/paho/).
 * 
 * Eclipse Paho is an umbrella project for several MQTT and MQTT-SN client implementations. 
 * This project was one of the first open source MQTT client implementations available and 
 * is actively maintained by a huge community. 
 * 
 * Paho features a Java client which is suited for embedded use, Android applications and 
 * Java applications in general. The initial code base was donated to Eclipse by IBM in 2012.
 * 
 * The Eclipse Paho Java client is rock-solid and is used by a broad range of companies from 
 * different industries around the world to connect to MQTT brokers. 
 * 
 * The synchronous/blocking API of Paho makes it easy to implement the applications MQTT logic 
 * in a clean and concise way while the asynchronous API gives the application developer full 
 * control for high-performance MQTT clients that need to handle high throughput. 
 * 
 * The Paho API is highly callback based and allows to hook in custom business logic to different 
 * events, e.g. when a message is received or when the connection to the broker was lost. 
 * 
 * Paho supports all MQTT features and a secure communication with the MQTT Broker is possible 
 * via TLS.
 */
class MqttInputDStream(
    _ssc: StreamingContext,
    properties:Properties,
    storageLevel: StorageLevel) extends ReceiverInputDStream[MqttEvent](_ssc) {

  override def name: String = s"MQTT stream [$id]"

  def getReceiver(): Receiver[MqttEvent] = {
    new MqttReceiver(properties, storageLevel)
  }
}

class MqttReceiver(
  properties:Properties,
  storageLevel: StorageLevel) extends Receiver[MqttEvent](storageLevel) {

  private val pahoOptions = new PahoOptions(properties)

  def onStop() {

  }
  /*
   * A use case for this receiver, e.g. is the The Thing Stack
   * application server that exposes LoRAWAN devices via MQTT 
   */
  def onStart() {

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

    val brokerUrl = pahoOptions.getBrokerUrl
    val clientId  = pahoOptions.getClientId

    val client = new MqttClient(brokerUrl, clientId,persistence)

    /* Initialize mqtt parameters */
    val options = pahoOptions.getMqttOptions

    /* 
     * Callback automatically triggers as and when new message 
     * arrives on specified topic 
     */    
    val callback = new MqttCallback() {

      override def messageArrived(topic: String, message: MqttMessage) {

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
         
          val result = new MqttEvent(timestamp, seconds, topic, qos, duplicate, retained, payload, digest, json, context, dimension)
          store(result)
          
        }
        
      }

      override def deliveryComplete(token: IMqttDeliveryToken) {
      }

      override def connectionLost(cause: Throwable) {
        restart("Connection lost ", cause)
      }
      
    }

    /* 
     * Set up callback for MqttClient. This needs to happen before
     * connecting or subscribing, otherwise messages may be lost
     */
    client.setCallback(callback)

    /* Connect to MqttBroker */
    client.connect(options)

    val qos    = pahoOptions.getQos
    val topics = pahoOptions.getTopics

    val quality = topics.map(_ => qos)
    client.subscribe(topics, quality)

  }

}
