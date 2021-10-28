package de.kp.works.stream.opencti
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

import java.util.Properties

object CTIClient {

  def build(properties:Properties, store:String => Unit): CTIClient =
    new CTIClient(properties, store)

}

class CTIClient(properties:Properties, store:String => Unit) {

  private var listener:CTIListener = _
  private val options = new CTIOptions(properties)

  def disconnect():Unit = {
    if (listener != null) listener.stop()
  }

  def connect():Unit = {

    val numThreads = options.getNumThreads
    val monitor = new CTIMonitor(options, new CTIHandler(options, store))

    listener = new CTIListener(monitor, numThreads)
    listener.start()

  }

}
