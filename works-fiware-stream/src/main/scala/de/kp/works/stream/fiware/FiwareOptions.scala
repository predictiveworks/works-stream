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

  def getBinding:HttpBinding = ???

  def getBrokerUrl:String = ???

  def isFiwareSsl:Boolean = ???

  def getFiwareSslOptions:Option[SslOptions] = ???

  def getNumThreads:Int = ???

  def isServerSsl:Boolean = ???

  def getServerSslOptions:Option[SslOptions] = ???

  def getSubscriptions:JsonArray = ???

}
