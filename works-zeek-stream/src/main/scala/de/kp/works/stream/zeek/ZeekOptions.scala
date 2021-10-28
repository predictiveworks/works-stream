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

import java.util.Properties
import scala.collection.JavaConverters._

class ZeekOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  /*
   * The parent file system folder path that contains
   * Zeek log files
   */
  def getLogFolder:String =
    settings(ZeekNames.LOG_FOLDER)

  /*
   * The file name postfix used to identify Zeek log
   * files
   */
  def getLogPostfix:String = {
    settings.getOrElse(ZeekNames.LOG_POSTFIX, ".log")
  }
  /*
   * The buffer size of the monitor for file system
   */
  def getMaxBufferSize:Int =
    settings.getOrElse(ZeekNames.MAX_BUFFER_SIZE, "1000").toInt

  /*
   * The maximum number of bytes of a file line
   */
  def getMaxLineSize:Int =
    settings.getOrElse(ZeekNames.MAX_LINE_SIZE, "8192").toInt

  def getNumThreads:Int =
    settings.getOrElse(ZeekNames.NUM_THREADS, "1").toInt

  /*
   * The polling interval in seconds
   */
  def getPollingInterval:Int =
    settings.getOrElse(ZeekNames.POLLING_INTERVAL, "1").toInt
  /*
   * The name of the Akka actor system that defines the
   * backend of this receiver
   */
  def getSystemName:String = "zeek-monitor"

}
