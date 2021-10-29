package de.kp.works.stream.pubsub
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
import com.google.api.client.auth.oauth2.Credential
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.api.java.{JavaInputDStream, JavaStreamingContext}
import org.apache.spark.streaming.dstream.InputDStream

object PubSubStream {
  
  /********** JAVA **********/

  /**
   *
   * @param jssc         JavaStreamingContext object
   * @param project      Google cloud project id
   * @param subscription Subscription name to subscribe to
   * @param credential   GCPCredentials to use for authenticating
   * @return
   */
  def createDirectStream(
      jssc: JavaStreamingContext,
      project: String, 
      subscription: String,
      credential: Credential): JavaInputDStream[PubSubResult] = {
    
    new JavaInputDStream(createDirectStream(
      jssc.ssc, StorageLevel.MEMORY_ONLY, project, None, subscription, credential))
  
  }
 
  /**
   *
   * @param jssc         JavaStreamingContext object
   * @param project      Google cloud project id
   * @param subscription Subscription name to subscribe to
   * @param topic        Topic name for creating subscription if need
   * @param credential   GCPCredentials to use for authenticating
   * @return
   */
  def createDirectStream(
      jssc: JavaStreamingContext,
      project: String, 
      subscription: String,
      topic: String,
      credential: Credential): JavaInputDStream[PubSubResult] = {
    
    new JavaInputDStream(createDirectStream(
      jssc.ssc, StorageLevel.MEMORY_ONLY, project, Option(topic), subscription, credential))
  
  }
 
  /**
   *
   * @param jssc         JavaStreamingContext object
   * @param storageLevel RDD storage level
   * @param project      Google cloud project id
   * @param subscription Subscription name to subscribe to
   * @param credential   GCPCredentials to use for authenticating
   * @return
   */
  def createDirectStream(
      jssc: JavaStreamingContext,
      storageLevel: StorageLevel,
      project: String, 
      subscription: String,
      credential: Credential): JavaInputDStream[PubSubResult] = {
    
    new JavaInputDStream(createDirectStream(
      jssc.ssc, storageLevel, project, None, subscription, credential))
  
  }
 
  /**
   *
   * @param jssc         JavaStreamingContext object
   * @param storageLevel RDD storage level
   * @param project      Google cloud project id
   * @param subscription Subscription name to subscribe to
   * @param topic        Topic name for creating subscription if need
   * @param credential   GCPCredentials to use for authenticating
   * @return
   */
  def createDirectStream(
      jssc: JavaStreamingContext,
      storageLevel: StorageLevel,
      project: String, 
      subscription: String,
      topic: String,
      credential: Credential): JavaInputDStream[PubSubResult] = {
    
    new JavaInputDStream(createDirectStream(
      jssc.ssc, storageLevel, project, Option(topic), subscription, credential))
  
  }

  /********** SCALA **********/

  /**
   * If topic is given, and the subscription doesn't exist,
   * create subscription by the given name.
   * 
   * Note: This Receiver will only receive the message arrived 
   * after the subscription created. If topic is not given, throw
   * not found exception when it doesn't exist
   * 
   * @param ssc             StreamingContext object
   * @param storageLevel    RDD storage level. Defaults to StorageLevel.MEMORY_ONLY.
   * @param project         Google cloud project id
   * @param topic           Topic name for creating subscription if need
   * @param subscription    Subscription name to subscribe to
   * @param credential      GCPCredentials to use for authenticating
   * @param autoAcknowledge Auto acknowledge incoming messages
   */
  def createDirectStream(
      ssc: StreamingContext,
      storageLevel: StorageLevel = StorageLevel.MEMORY_ONLY,
      project: String,
      topic: Option[String],
      subscription: String,
      credential: Credential,
      autoAcknowledge: Boolean = true): InputDStream[PubSubResult] = {
    
    new PubSubInputDStream(ssc, storageLevel, project, topic, subscription, credential, autoAcknowledge)

  }

}