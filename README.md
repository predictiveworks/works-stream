# Works Stream

This project complements Apache Spark Streaming with hand-picked streaming receivers.

## MQTT
TBD

## Server Sent Events (SSE)

### Internet of Things

FIWARE brings a curated framework of open source software components to accelerate and 
ease the implementation of smart IoT platforms and solutions.

The main components comprise and information hub, the FIWARE Context Broker, and a set of
IoT Agents (IOTA) to interact with devices via widely used IoT protocols and bridge between
multiple message formats and a common *NGSI v2* and *NGSI-LD* based format.

From an analytics perspective, it is to connect to a (single) Context Broker and receive
device notification in real-time and in NGSI format, instead of interacting with plenty of
individual data sources.

Just to name a few IoT protocols supported by the FIWARE framework:
  * ISOXML
  * LoRaWAN
  * LWM2M over CoaP
  * MQTT  
  * OPC-UA
  * Sigfox

FIWARE is not restricted to these protocols, and also provides an agent library to build
custom IoT Agents.

**PredictiveWorks.** ships with FIWARE Beat, a standalone Akka based Http(s) service to 
subscribe to a FIWARE Context Broker, receive Broker notification in NSGI, and publish
these notifications as Server Sent Events.

**Works Stream** is a library to receive SSE from a Http(s) service and publish these
events as an Apache Spark event stream.

**PredictiveWorks.** uses *Work Stream* to consume SSE with a Google **CDAP** streaming
source as a starting point for complex real-time event processing covering descriptive,
diagnostic, predictive and prescriptive analytics.

<p align="center">
  <img src="https://github.com/predictiveworks/works-stream/blob/main/images/works-stream-2021-08-18.png" width="600" alt="Works Stream">
</p>



### Threat Intelligence
TBD