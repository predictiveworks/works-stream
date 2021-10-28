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

import com.google.gson.JsonObject

class ThingsEvent(
    /* The timestamp in milli seconds 
     * the message arrived 
     */
    val timestamp: Long,
    /* The timestamp in seconds the
     * message arrived
     */
    val seconds: Long,
    /* The MQTT topic of the message 
     */
    val topic: String,
    /* The quality of service of the message
     */
    val qos: Int,
    /* Indicates whether or not this message might be a
     * duplicate of one which has already been received.
     */
    val duplicate: Boolean,
    /* Indicates whether or not this message should be/was 
     * retained by the server.
     */
    val retained: Boolean,
    /* The payload of this message
     */    
    val payload: Array[Byte],
    /* The MD5 digest of topic and payload
     * to identify duplicate messages
     */
    val digest: String,
    /* The [String] representation of the
     * payload
     */
    val json: String,
    /* The context of the message, i.e. all
     * topic levels except the last one as
     * MD5 digest
     */
    val context: String,
    /* The lowest topic level, which describes
     * semantic meaning of message
     */
    val dimension: String) extends Serializable {
  
  def copy():ThingsEvent = {

    new ThingsEvent(
      timestamp,
      seconds,
      topic,
      qos,
      duplicate,
      retained,
      payload,
      digest,
      json,
      context,
      dimension)

  }

  def toJson:JsonObject = {

    val mqttObj = new JsonObject
    mqttObj.addProperty("timestamp", timestamp)

    mqttObj.addProperty("seconds", seconds)
    mqttObj.addProperty("topic", topic)

    mqttObj.addProperty("qos", qos)
    mqttObj.addProperty("duplicate", duplicate)

    mqttObj.addProperty("retained" ,retained)
    mqttObj.addProperty("digest", digest)

    mqttObj.addProperty("payload", json)
    mqttObj.addProperty("context", context)

    mqttObj.addProperty("dimension", dimension)

    mqttObj

  }
}