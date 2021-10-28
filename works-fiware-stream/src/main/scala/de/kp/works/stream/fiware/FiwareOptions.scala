package de.kp.works.stream.fiware
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

import com.google.gson.JsonArray
import de.kp.works.stream.ssl.SslOptions

import java.util.Properties
import scala.collection.JavaConverters._

case class HttpBinding(host:String, port:Int)

class FiwareOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  /**
   * ACTOR SPECIFIC CONFIGURATIONS
   */

  /**
   * Timeout in seconds
   */
  def getActorTimeout:Int = 5
  /**
   * Parameters to control the handling of failed child actors:
   * it is the number of retries within a certain time window.
   *
   * The supervisor strategy restarts a child up to 10 restarts
   * per minute. The child actor is stopped if the restart count
   * exceeds maxRetries during the withinTimeRange duration.
   *
   */
  def getActorMaxRetries:Int = 10
  /**
   * Time range in minutes
   */
  def getActorTimeRange:Int = 1
  /**
   * Child actors are defined leveraging a RoundRobin pool with a
   * dynamic resizer. The boundaries of the resizer are defined
   * below
   */
  def getActorLowerBound = 2
  def getActorUpperBound = 100

  /**
   * The number of instances for the RoundRobin pool
   */
  def getActorInstances = 50

  /**
   * EXTERNAL CONFIGURATION
   */
  def getBinding:HttpBinding = ???

  def getBrokerUrl:String = ???

  def isFiwareSsl:Boolean = ???

  def getFiwareSslOptions:Option[SslOptions] = ???

  def getNumThreads:Int = ???

  def isServerSsl:Boolean = ???

  def getServerSslOptions:Option[SslOptions] = ???

  def getSubscriptions:JsonArray = ???

}
