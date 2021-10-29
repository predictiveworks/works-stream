package de.kp.works.stream.mqtt.hivemq
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

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.api.java.{JavaReceiverInputDStream, JavaStreamingContext}
import org.apache.spark.streaming.dstream.ReceiverInputDStream

import java.util.Properties

object HiveStream {
  
  /********** JAVA **********/
  
  /**
   * Storage level of the data will be the default 
   * StorageLevel.MEMORY_AND_DISK_SER_2.
   */
  def createDirectStream(
      jssc: JavaStreamingContext,
      properties: Properties): JavaReceiverInputDStream[String] = {

    createDirectStream(jssc, properties, StorageLevel.MEMORY_AND_DISK_SER_2)
    
  }

  def createDirectStream(
      jssc: JavaStreamingContext,
      properties: Properties,
      storageLevel: StorageLevel): JavaReceiverInputDStream[String] = {

    createDirectStream(jssc.ssc, properties, storageLevel)
    
  }

  /********** SCALA **********/

  def createDirectStream(
    ssc: StreamingContext,
    properties: Properties,
    storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2): ReceiverInputDStream[String] = {

    new HiveMQInputDStream(ssc, properties, storageLevel)

  }

}
