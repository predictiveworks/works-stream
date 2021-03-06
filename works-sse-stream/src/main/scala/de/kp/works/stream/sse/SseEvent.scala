package de.kp.works.stream.sse

import com.google.gson.JsonObject

/*
 * Copyright (c) 2021 Dr. Krusche & Partner PartG. All rights reserved.
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

class SseEvent(
    val sseId  :String,
    val sseType:String,
    val sseData:String) extends Serializable {
  
  def copy:SseEvent = {
    new SseEvent(sseId, sseType, sseData)
  }

  def toJson:String = {

    val sseObj = new JsonObject
    sseObj.addProperty("id", sseId)

    sseObj.addProperty("type", sseType)
    sseObj.addProperty("data", sseData)

    sseObj.toString

  }

}