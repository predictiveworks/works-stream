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

import java.util.concurrent.{ExecutorService, Executors}

class ZeekListener(monitor:ZeekMonitor, numThreads:Int = 1) {

  private var executorService:ExecutorService = _

  def start():Unit = {

    val worker = new Runnable {

      override def run(): Unit = {

        val now = new java.util.Date().toString
        println(s"[ZeekListener] $now - Listener worker started.")

        monitor.start()

      }
    }

    try {

      /* Initiate stream execution */
      executorService = Executors.newFixedThreadPool(numThreads)
      executorService.execute(worker)

    } catch {
      case _:Exception => executorService.shutdown()
    }

  }

  def stop():Unit = {

    /* Stop listening to the Fleet log events stream  */
    executorService.shutdown()
    executorService.shutdownNow()

  }

}
