package de.kp.works.stream.mqtt.paho
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

import de.kp.works.stream.mqtt.MqttEvent
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.api.java.{JavaReceiverInputDStream, JavaStreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream

import java.util.Properties

object PahoStream {

  /********** JAVA **********/

  /**
   * Storage level of the data will be the default
   * StorageLevel.MEMORY_AND_DISK_SER_2.*
   */
  def createStream(
    jssc: JavaStreamingContext,
    properties: Properties): JavaReceiverInputDStream[MqttEvent] = {

    createStream(jssc.ssc, properties, StorageLevel.MEMORY_AND_DISK_SER_2)

  }

  def createStream(
    jssc: JavaStreamingContext,
    properties: Properties,
    storageLevel: StorageLevel): JavaReceiverInputDStream[MqttEvent] = {

    createStream(jssc.ssc, properties, storageLevel)

  }

  /********** SCALA **********/

   def createStream(
    ssc: StreamingContext,
    properties:Properties,
    storageLevel: StorageLevel): ReceiverInputDStream[MqttEvent] = {

    new MqttInputDStream(ssc, properties, storageLevel)

   }

}
