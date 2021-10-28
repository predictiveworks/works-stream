package de.kp.works.stream.sse
/*
 * Copyright (c) 2020 Dr. Krusche & Partner PartG. All rights reserved.
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

class SseOptions(properties:Properties) {

  val settings:Map[String,String] = properties.asScala.toMap

  def getAuthToken:Option[String] =
    settings.get(SseNames.AUTH_TOKEN)

  def getNumThreads:Int = {
    settings.getOrElse(SseNames.NUM_THREADS, "1").toInt
  }

  def getServerUrl:String =
    settings(SseNames.SERVER_URL)

  def getSslOptions:Option[SslOptions] = {

    try {
      val tlsVersion = settings
        .getOrElse(SseNames.SSL_PROTOCOL, "TLS")

      /*
       * The keystore file must be defined
       */
      val keyStoreFile = settings
        .getOrElse(SseNames.SSL_KEYSTORE_FILE,
          throw new Exception(s"No keystore file specified."))
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val keyStoreType = settings
        .getOrElse(SseNames.SSL_KEYSTORE_TYPE, "JKS")

      val keyStorePass = settings
        .getOrElse(SseNames.SSL_KEYSTORE_PASS, "")

      val keyStoreAlgo = settings
        .getOrElse(SseNames.SSL_KEYSTORE_ALGO, "SunX509")

      val trustStoreFile = settings.get(SseNames.SSL_TRUSTSTORE_FILE)
      /*
       * - JKS      Java KeyStore
       * - JCEKS    Java Cryptography Extension KeyStore
       * - PKCS #12 Public-Key Cryptography Standards #12 KeyStore
       * - BKS      Bouncy Castle KeyStore
       * - BKS-V1   Older and incompatible version of Bouncy Castle KeyStore
       */
      val trustStoreType = settings
        .getOrElse(SseNames.SSL_TRUSTSTORE_TYPE, "JKS")

      val trustStorePass = settings
        .getOrElse(SseNames.SSL_TRUSTSTORE_PASS, "")

      val trustStoreAlgo = settings
        .getOrElse(SseNames.SSL_TRUSTSTORE_ALGO, "SunX509")

      val cipherSuites = settings
        .getOrElse(SseNames.SSL_CIPHER_SUITES, "")
        .split(",").map(_.trim).toList

      val sslOptions = if (trustStoreFile.isEmpty) {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          cipherSuites
        )
      }
      else {
        SslOptions.Builder.buildStoreOptions(
          tlsVersion,
          keyStoreFile,
          keyStoreType,
          keyStorePass,
          keyStoreAlgo,
          trustStoreFile.get,
          trustStoreType,
          trustStorePass,
          trustStoreAlgo,
          cipherSuites)

      }

      Some(sslOptions)

    } catch {
      case _:Throwable => None
    }

  }

}
