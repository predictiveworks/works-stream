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
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver

import java.util.Properties

class FiwareInputDStream(
  ssc_ : StreamingContext,
  properties: Properties,
  storageLevel: StorageLevel) extends ReceiverInputDStream[String](ssc_) {
  
  override def name: String = s"FIWARE notification stream [$id]"
  
  def getReceiver(): Receiver[String] = {
    new FiwareReceiver(properties, storageLevel)
  }

}

class FiwareReceiver(
    properties: Properties,
    storageLevel: StorageLevel) extends Receiver[String](storageLevel) {

  private var client:FiwareClient = _

  def onStop() {
    if (client != null) client.disconnect()
  }

  def onStart() {
    client = FiwareClient.build(properties, store)
  }

}