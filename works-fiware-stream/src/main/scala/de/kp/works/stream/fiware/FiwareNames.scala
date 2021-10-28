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

object FiwareNames {

  val NUM_THREADS = "fiware.num.threads"
  /*
   * The HTTP binding for the Fiware notification
   * server
   */
  val SERVER_HOST = "fiware.server.host"
  val SERVER_PORT = "fiware.server.port"

  val SERVER_SSL_CIPHER_SUITES   = "fiware.server.ssl.cipher.suites"
  val SERVER_SSL_KEYSTORE_ALGO   = "fiware.server.ssl.keystore.algorithm"
  val SERVER_SSL_KEYSTORE_FILE   = "fiware.server.ssl.keystore.file"
  val SERVER_SSL_KEYSTORE_PASS   = "fiware.server.ssl.keystore.password"
  val SERVER_SSL_KEYSTORE_TYPE   = "fiware.server.ssl.keystore.type"
  val SERVER_SSL_PROTOCOL        = "fiware.server.ssl.protocol"
  val SERVER_SSL_TRUSTSTORE_ALGO = "fiware.server.ssl.truststore.algorithm"
  val SERVER_SSL_TRUSTSTORE_FILE = "fiware.server.ssl.truststore.file"
  val SERVER_SSL_TRUSTSTORE_PASS = "fiware.server.ssl.truststore.password"
  val SERVER_SSL_TRUSTSTORE_TYPE = "fiware.server.ssl.truststore.type"

  /*
   * The Fiware Context Broker configuration
   */
  val BROKER_URL           = "fiware.broker.url"
  val BROKER_SUBSCRIPTIONS = "fiware.broker.subscriptions"

  val BROKER_SSL_CIPHER_SUITES   = "fiware.broker.ssl.cipher.suites"
  val BROKER_SSL_KEYSTORE_ALGO   = "fiware.broker.ssl.keystore.algorithm"
  val BROKER_SSL_KEYSTORE_FILE   = "fiware.broker.ssl.keystore.file"
  val BROKER_SSL_KEYSTORE_PASS   = "fiware.broker.ssl.keystore.password"
  val BROKER_SSL_KEYSTORE_TYPE   = "fiware.broker.ssl.keystore.type"
  val BROKER_SSL_PROTOCOL        = "fiware.broker.ssl.protocol"
  val BROKER_SSL_TRUSTSTORE_ALGO = "fiware.broker.ssl.truststore.algorithm"
  val BROKER_SSL_TRUSTSTORE_FILE = "fiware.broker.ssl.truststore.file"
  val BROKER_SSL_TRUSTSTORE_PASS = "fiware.broker.ssl.truststore.password"
  val BROKER_SSL_TRUSTSTORE_TYPE = "fiware.broker.ssl.truststore.type"

}
