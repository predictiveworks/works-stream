package de.kp.works.stream.zeek
/*
 * Copyright (c) 2019 Dr. Krusche & Partner PartG. All rights reserved.
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

import java.util.Properties

object ZeekClient {

  def build(properties:Properties): ZeekClient =
    new ZeekClient(properties)

}

class ZeekClient(properties:Properties) {

  private var receiver:ZeekReceiver = _
  private val zeekOptions = new ZeekOptions(properties)

  def disconnect():Unit = {

  }

  def connect():Unit = {

  }

  def setReceiver(receiver:ZeekReceiver):Unit = {
    this.receiver = receiver
  }
}
