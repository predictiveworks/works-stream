package de.kp.works.stream.zeek

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

import com.google.gson.{JsonElement, JsonObject}

class ZeekHandler(options:ZeekOptions, store:String => Unit) {

  def sendFileEvent(fileName:String, fileElement:JsonElement):Unit = {
    /*
     * The current implementation sends an SSE-like event
     */
    val eventObj = new JsonObject
    eventObj.addProperty("id", "zeek")

    eventObj.addProperty("type", fileName)
    eventObj.addProperty("data", fileElement.toString)

    store(eventObj.toString)

  }

}
