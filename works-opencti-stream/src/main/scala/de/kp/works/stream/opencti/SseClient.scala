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

import de.kp.works.stream.ssl.SslUtil
import okhttp3._

import javax.net.ssl.SSLContext

class SseClient(options:CTIOptions) {
  /**
   * This is an internal helper method to create an OkHttpClient
   * that trusts all certificates
   */
  private def createUnsafeClient:OkHttpClient = {

    try {

      val allTrustManagers = SslUtil.getAllTrustManagers
      val sslContext =  SSLContext.getInstance("TLS")

      sslContext.init(null, allTrustManagers, new java.security.SecureRandom())
      val sslSocketFactory = sslContext.getSocketFactory

      val builder = new OkHttpClient.Builder()

      val x509TrustManager = allTrustManagers(0).asInstanceOf[javax.net.ssl.X509TrustManager]
      builder.sslSocketFactory(sslSocketFactory, x509TrustManager)

      builder.build

    } catch {
      case _:Throwable => null
    }

  }
  /**
   * This is an internal method to create an OkHttpClient
   * that leverages SSL socket security and optionally uses
   * an authentication token for authorization
   */
  private def createSafeClient:OkHttpClient = {

    try {

      val sslOptions = options.getSslOptions.get

      val sslSocketFactory = sslOptions.getSslSocketFactory
      val x509TrustManager = sslOptions.getTrustManagerFactory.getTrustManagers()(0).asInstanceOf[javax.net.ssl.X509TrustManager]

      val builder = new OkHttpClient.Builder()
      builder.sslSocketFactory(sslSocketFactory, x509TrustManager)

      builder.build

    } catch {
      case _:Throwable => null
    }

  }

  def getHttpClient:OkHttpClient = {

    val sslOptions = options.getSslOptions

    if (sslOptions.isDefined)
      createSafeClient

    else
      createUnsafeClient

  }

  def getRequest:Request = {
    /*
     * Build request with an optional authentication token
     */
    val builder = new Request.Builder()
      .url(options.getServerUrl)

    val authToken = options.getAuthToken

    val request = {
      if (authToken.isDefined)
        builder
          .addHeader("Authorization", "Bearer " + authToken.get)
      else
        builder

    }.build

    request

  }

}
