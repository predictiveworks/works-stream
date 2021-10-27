package de.kp.works.stream.ditto
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

import com.google.gson.Gson
import org.eclipse.ditto.client.changes.{FeatureChange, FeaturesChange, ThingChange}
import org.eclipse.ditto.client.live.messages.RepliableMessage
import org.eclipse.ditto.client.messaging.MessagingProvider

import java.util.Properties
import org.eclipse.ditto.client.{DittoClient => EclipseClient, DittoClients => EclipseClients}
import org.eclipse.ditto.model.base.common.HttpStatusCode
import org.eclipse.ditto.model.things.ThingId

object DittoClient {

  def build(properties:Properties):DittoClient =
    new DittoClient(properties)

}

class DittoClient(properties:Properties) {

  private var client:EclipseClient = _
  private var receiver:DittoReceiver = _

  def setReceiver(receiver:DittoReceiver):Unit = {
    this.receiver = receiver
  }

  def disconnect(): Unit = {

    if (client != null) {

      /** CHANGE EVENTS **/

      client.twin().suspendConsumption()

      if (properties.containsKey(DittoNames.DITTO_THING_CHANGES)) {

        val flag = properties.getProperty(DittoNames.DITTO_THING_CHANGES)
        if (flag == "true") client.twin().deregister(DittoNames.DITTO_THING_CHANGES_HANDLER)

      }
      if (properties.containsKey(DittoNames.DITTO_FEATURES_CHANGES)) {

        val flag = properties.getProperty(DittoNames.DITTO_FEATURES_CHANGES)
        if (flag == "true") client.twin().deregister(DittoNames.DITTO_FEATURES_CHANGES_HANDLER)

      }
      if (properties.containsKey(DittoNames.DITTO_FEATURE_CHANGES)) {

        val flag = properties.getProperty(DittoNames.DITTO_FEATURE_CHANGES)
        if (flag == "true") client.twin().deregister(DittoNames.DITTO_FEATURE_CHANGES_HANDLER)

      }

      /** LIVE MESSAGES **/

      client.live().suspendConsumption()

      if (properties.containsKey(DittoNames.DITTO_LIVE_MESSAGES)) {

        val flag = properties.getProperty(DittoNames.DITTO_LIVE_MESSAGES)
        if (flag == "true") client.live().deregister(DittoNames.DITTO_LIVE_MESSAGES_HANDLER)

      }

      client.destroy()

    }

  }

  def connect():Unit = {
    /*
     * Build Ditto web socket client
     */
    val messagingProvider:MessagingProvider = DittoHelper.getMessagingProvider(properties)
    client = EclipseClients.newInstance(messagingProvider)
    /*
     * This Ditto web socket client subscribes to two protocol commands:
     *
     * - PROTOCOL_CMD_START_SEND_EVENTS   :: "START-SEND-EVENTS"
     * - PROTOCOL_CMD_START_SEND_MESSAGES :: "START-SEND-MESSAGES"
     *
     * Subscription to events is based on Ditto's twin implementation
     * and refers to the TwinImpl.CONSUME_TWIN_EVENTS_HANDLER which
     * is initiated in the twin's doStartConsumption method
     *
     * Subscription to events is based on Ditto's live implementation
     * and refers to the LiveImpl.CONSUME_LIVE_MESSAGES_HANDLER which
     * is initiated in the live's doStartConsumption method
     *
     */

    registerForTwinEvents()
    registerForLiveMessages()

    client.twin().startConsumption().get() // EVENTS
    client.live().startConsumption().get() // MESSAGES

  }

  private def registerForTwinEvents() {

    val twin = client.twin()
    /*
     * Check whether a certain thing identifier is provided to
     * restrict events to a certain thing
     */
    val thingId = if (properties.containsKey(DittoNames.DITTO_THING_ID)) {
      ThingId.of(properties.getProperty(DittoNames.DITTO_THING_ID))

    } else null
    /*
     * Check whether a certain feature identifier is provided to
     * restrict events to a certain feature
     */
    val featureId = if (properties.containsKey(DittoNames.DITTO_FEATURE_ID)) {
      properties.getProperty(DittoNames.DITTO_FEATURE_ID)

    } else null

    /***** THING CHANGES *****/

    if (properties.containsKey(DittoNames.DITTO_THING_CHANGES)) {

      val flag = properties.getProperty(DittoNames.DITTO_THING_CHANGES)
      if (flag == "true") {

        val consumer = new java.util.function.Consumer[ThingChange] {
          override def accept(change:ThingChange):Unit = {

            val gson = DittoGson.thing2Gson(change)
            if (gson != null) receiver.store(gson)

          }
        }

        val handler = DittoNames.DITTO_THING_CHANGES_HANDLER

        if (thingId == null) {

          /* Register for changes of all things */
          twin.registerForThingChanges(handler, consumer)

        } else {

          /* Register for changes of thing with thingId */
          twin.forId(thingId).registerForThingChanges(handler, consumer)

        }

      }

    }

    /***** FEATURES CHANGES *****/

    if (properties.containsKey(DittoNames.DITTO_FEATURES_CHANGES)) {

      val flag = properties.getProperty(DittoNames.DITTO_FEATURES_CHANGES)
      if (flag == "true") {

        val consumer = new java.util.function.Consumer[FeaturesChange] {
          override def accept(change:FeaturesChange):Unit = {

            val gson = DittoGson.features2Gson(change)
            receiver.store(gson)

          }
        }

        val handler = DittoNames.DITTO_FEATURES_CHANGES_HANDLER

        if (thingId == null) {
          /*
           * Register feature set changes of all things, as we currently
           * do not support the provisioning of a certain thing
           */
          twin.registerForFeaturesChanges(handler, consumer)

        } else {
          /*
           * Register feature set changes of all things, as we currently
           * do not support the provisioning of a certain thing
           */
          twin.forId(thingId).registerForFeaturesChanges(handler, consumer)

        }
      }

    }

    /***** FEATURE CHANGES *****/

    if (properties.containsKey(DittoNames.DITTO_FEATURE_CHANGES)) {
      /*
       * Register for all feature changes of all things
       */
      val flag = properties.getProperty(DittoNames.DITTO_FEATURE_CHANGES)
      if (flag == "true") {

        val consumer = new java.util.function.Consumer[FeatureChange] {
          override def accept(change:FeatureChange):Unit = {

            val gson = DittoGson.feature2Gson(change)
            receiver.store(new Gson().toJson(gson))

          }
        }

        val handler = DittoNames.DITTO_FEATURE_CHANGES_HANDLER

        if (thingId != null) {

          if (featureId != null) {
            twin.registerForFeatureChanges(handler, featureId, consumer)

          } else
            twin.registerForFeatureChanges(handler, consumer)

        } else {

          if (featureId != null)
            twin.forId(thingId).registerForFeatureChanges(handler, featureId, consumer)

          else
            twin.forId(thingId).registerForFeatureChanges(handler, consumer)

        }

      }

    }

  }

  private def registerForLiveMessages() {

    val live = client.live()
    /*
     * Check whether a certain thing identifier is provided to
     * restrict events to a certain thing
     */
    val thingId = if (properties.containsKey(DittoNames.DITTO_THING_ID)) {
      ThingId.of(properties.getProperty(DittoNames.DITTO_THING_ID))

    } else null

    if (properties.containsKey(DittoNames.DITTO_LIVE_MESSAGES)) {

      val flag = properties.getProperty(DittoNames.DITTO_LIVE_MESSAGES)
      if (flag == "true") {

        val consumer = new java.util.function.Consumer[RepliableMessage[String, Any]] {
          override def accept(message:RepliableMessage[String, Any]) {

            val gson = DittoGson.message2Gson(message)
            if (gson != null) {

              receiver.store(gson)
              message.reply().statusCode(HttpStatusCode.OK).send()

            } else {
              message.reply().statusCode(HttpStatusCode.NO_CONTENT).send()

            }

          }
        }

        val handler = DittoNames.DITTO_LIVE_MESSAGES

        if (thingId != null) {
          live.forId(thingId).registerForMessage(handler, "*", classOf[String], consumer)

        } else {
          live.registerForMessage(handler, "*", classOf[String], consumer)

        }

      }

    }

  }

}
