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

import java.util.concurrent.{ExecutorService, Executors}

/**
 * OpenCTI is currently using REDIS Stream as its technical layer.
 * Each time data is modified in the OpenCTI database, a specific
 * event is added in the stream.
 *
 * In order to provides a really easy consuming protocol OpenCTI
 * provides an SSE endpoint.
 *
 * Every user with respective access rights can open and access
 *
 * http(s)://[host]:[port]/stream]
 *
 * and open an SSE connection to start receiving live events.
 *
 * The [CTIMonitor] leverages an SSE client to connect to the
 * exposed SSE endpoint and listens to the OpenCTI event stream.
 */
class CTIListener(monitor:CTIMonitor, numThreads:Int = 1) {

  private var executorService:ExecutorService = _

  def start():Unit = {
    /*
     * Wrap connector and output handler in a runnable
     */
    val worker = new Runnable {

      override def run(): Unit = {

        val now = new java.util.Date().toString
        println(s"[CTIListener] $now - Receiver worker started.")

        monitor.start()

      }
    }

    try {

      /* Initiate stream execution */
      executorService = Executors.newFixedThreadPool(numThreads)
      executorService.execute(worker)

    } catch {
      case _:Throwable => executorService.shutdown()
    }

  }

  def stop():Unit = {

    /* Stop listening to the OpenCTI events stream  */
    executorService.shutdown()
    executorService.shutdownNow()

  }

}
