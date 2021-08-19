# Works Stream

This project complements Apache Spark Streaming with hand-picked streaming receivers.

## Eclipse Ditto

Eclipse Ditto is a technology in the IoT implementing a software pattern called **digital twins**.
A digital twin is a virtual, cloud based, representation of his real world counterpart (real world
Things, e.g. devices like sensors, smart heating, connected cars, smart grids, EV charging stations etc).

The technology mirrors potentially millions and billions of digital twins residing in the digital world 
with physical **Things**. This simplifies developing IoT solutions for software developers as they do not 
need to know how or where exactly the physical Things are connected.

With Ditto a thing can just be used as any other web service via its digital twin.

Eclipse Ditto adds a semantic layer to the MQTT 3.1.1 and MQTT 5 and ease the analysis of MQTT based
device reading.

**Works Stream** is a library to connect to an Eclipse Ditto service and register for both, *twin events* 
and *live messages*. Events and messages are published as an Apache Spark event stream.

**PredictiveWorks.** uses *Work Stream* to consume twin events and live messages  with a Google **CDAP** 
streaming source as a starting point for complex real-time event processing covering descriptive,
diagnostic, predictive and prescriptive analytics.

<p align="center">
  <img src="https://github.com/predictiveworks/works-stream/blob/main/images/works-stream-2021-08-18-3.png" width="600" alt="Works Stream">
</p>

## MQTT

### HiveMQ

HiveMQ is an MQTT broker, and a client based messaging platform designed for the fast, 
efficient and reliable movement of data to and from connected IoT devices. It uses the 
MQTT protocol for instant, bi-directional push of data between devices and the enterprise
system.

HiveMQ supports the MQTT 3.1, MQTT 3.1.1 and MQTT 5 specification.

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
  <img src="https://github.com/predictiveworks/works-stream/blob/main/images/works-stream-2021-08-18-1.png" width="600" alt="Works Stream">
</p>

### Threat Intelligence

OpenCTI is a unified open source platform for all levels of Cyber Threat Intelligence. A major goal
is to build and provide a powerful knowledge base for cyber threat intelligence and cyber operations.

OpenCTI ships with a variety of connectors to widely known threat intelligence data sources like
AlienVault, CrowdStrike, FireEye and MISP, MITRE ATT&CK and more.

**PredictiveWorks** ships with OpenCTI Beat, as standalone Akka based Http(s) service to consume
the OpenCTI events stream (SSE), applies moderate event transformations and republishes the threat
feed as Server Sent Events.

This approach enables **Works Stream** to consume both, events from IoT devices and threat intelligence.

Then OpenCTI threat events can be consumed with a Google **CDAP** streaming source and revealed to 
complex event analytics.

<p align="center">
  <img src="https://github.com/predictiveworks/works-stream/blob/main/images/works-stream-2021-08-18-2.png" width="600" alt="Works Stream">
</p>
