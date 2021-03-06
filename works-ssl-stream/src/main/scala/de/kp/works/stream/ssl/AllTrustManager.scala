package de.kp.works.stream.ssl
/*
 * Copyright (c) 2020 - 2021 Dr. Krusche & Partner PartG. All rights reserved.
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

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class AllTrustManager extends X509TrustManager {

  override def getAcceptedIssuers = Array.empty[X509Certificate]

  override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = {
  }

  override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = {
  }

}
