package de.kp.works.stream.opencti

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
import de.kp.works.stream.ssl.SslOptions
import java.util.Properties
import scala.collection.JavaConverters._

class CTIOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  def getAuthToken:Option[String] = ???

  def getNumThreads:Int = ???

  def getServerUrl:String = ???

  def getSslOptions:Option[SslOptions] = ???

}
