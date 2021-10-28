package de.kp.works.stream.fleet

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

object FleetNames {
  /*
   * The parent file system folder path that contains
   * Fleet log files
   */
  val LOG_FOLDER  = "fleet.folder"
  /*
   * The file name postfix used to identify Fleet log
   * files
   */
  val LOG_POSTFIX = "fleet.postfix"
  /*
   * The buffer size of the Fleet monitor for file system
   * events
   */
  val MAX_BUFFER_SIZE = "fleet.max.buffer.size"
  /*
   * The maximum number of bytes of a file line
   */
  val MAX_LINE_SIZE = "fleet.max.line.size"
  /*
   * The number of threads used by the Fleet listener
   */
  val NUM_THREADS = "fleet.num.threads"
  /*
   * The polling interval of the Fleet monitor in seconds
   */
  val POLLING_INTERVAL = "fleet.polling.interval"

}
