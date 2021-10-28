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

object ZeekNames {
  /*
   * The parent file system folder path that contains
   * Zeek log files
   */
  val LOG_FOLDER  = "zeek.folder"
  /*
   * The file name postfix used to identify Zeek log
   * files
   */
  val LOG_POSTFIX = "zeek.postfix"
  /*
   * The buffer size of the Zeek monitor for file system
   * events
   */
  val MAX_BUFFER_SIZE = "zeek.max.buffer.size"
  /*
   * The maximum number of bytes of a file line
   */
  val MAX_LINE_SIZE = "zeek.max.line.size"
  /*
   * The number of threads used by the Zeek listener
   */
  val NUM_THREADS = "zeek.num.threads"
  /*
   * The polling interval of the Zeek monitor in seconds
   */
  val POLLING_INTERVAL = "zeek.polling.interval"
}
