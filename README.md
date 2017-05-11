## A High Performance, Scalable Event-Bus System for Distributed Data Synchronization

### Design
 
 In this project, we basically focus to overcome the limitations of the existing techniques and our primary goal is to provide the support for fully transactional communication between the clients and the eventbus thereby making the communication more effective.

![a1](https://cloud.githubusercontent.com/assets/22176809/25928044/919785f0-35bf-11e7-99a5-2d106d0472d8.PNG)

  Above figure represents the complete architecture of our project where both the publisher and subscriber sends the handshake request in the form of a HTTP upgrade header with data about the EventBus WebSocket address it’s attempting to connect. Event bus responds to the request with another HTTP header, indicating handshake was successful, once the connection is established between the client(i.e., publisher/subscriber) and the event bus, publisher will be then able to publish the message to the event bus including all the required parameters with respect to the topic. The messages published by the publisher will be stored in the receive buffer at the event bus end. Once the event bus receives the message from the publisher, it checks for the information about all the subscribers and their registered topics. Now based on the topic_id obtained in the message from the publisher, the eventbus then filters the subscribers to whom the message should be delivered and thereby delivers the message to the respective subscriber using the sender buffer present at the event bus end. Once the subscriber receives the message it sends the acknowledgment to the event_bus that the message has been received. Also, the publisher will be able to send different events in parallel based on the topic id.
  
  We have serialized the message delivery between publisher-eventbus-subscriber by using ‘Avro’ which is one of the serialization techniques used in hadoop.
  
  ![a2](https://cloud.githubusercontent.com/assets/22176809/25928043/9196a1e4-35bf-11e7-97ff-b5ecdbe3c538.PNG)
  
  Above figure gives a clear picture on how we have handled the scalability with respect to different event buses. We have done the scalability for load balancing the messages at the event bus end i.e., whenever the messages in the first event bus reaches to a particular threshold value, message delivery will be automatically transferred onto a new event bus thereby balancing the load at the event bus end and assuring a smooth flow of messages between the publisher-eventbus and eventbus-subscriber. As the number of messages increase at the eventbus end leading to the buffer overflow, the current eventbus automatically switches to a new eventbus in order to balance the load at its end. 

### Implementation

  Websockets replaces the native socket and multithreading concepts of java, which is the efficient way of establishing the pub-sub connection. Specifically, Annotation endpoint classes such as clientendpoint and serverendpoint are used for http connections. Websocket clientendpoint is used by the client to connect to eventbus and serverendpoint is used by the eventbus for connecting to the server and serving the subscribers.

  Client (i.e., Subscriber/Publisher) connects to eventbus at the port number  and registers to the respective topic which is passed as the argument. Once the connection is established a websocket session will be created assigned with unique session id, Session info and the topic the subscriber is registered will be stored into the hashmap for the future reference at the eventbus end. Also there is a possibility for the subscriber to register to multiple topics. Therefore, if the subscriber register for multiple topics multiple entries will be create in the hashmap.

Below is the message format at the publisher end:

![1](https://cloud.githubusercontent.com/assets/22176809/25927921/dce75202-35be-11e7-9642-1252a3f1cccd.PNG)

Below is the message format at the eventbus end:

![2](https://cloud.githubusercontent.com/assets/22176809/25927930/f2fbffac-35be-11e7-894f-260fd34d1dae.PNG)

TypeCode used:

‘001’ to achieve the behaviour of the Publisher

‘002’ to achieve the behaviour of the Subscriber

  Once the publisher establishes the connection to the eventbus, a new thread will be created for publishing the messages to the eventbus. Publisher will send the message in the above message format while publishing the messages to the eventbus.
  
  Message will be serialized by using ‘Avro’ through which it is converted to the bytestream and writes into the sender buffer present at the eventbus end. All the Messages will be sent to eventbus  using session.getBasicRemote().sendObject(dataFileWriter). Once the message is received at the eventbus end, OnMessage event will be triggered at the eventbus once the publisher publishes the message to the eventbus. Once the OnMessage event is triggered, the topic of the message will be checked and the Hashmap will be scanned in order to get the subscriber's registered for that topic and thereby message will be written into the receiver buffer at the eventbus end and broadcasts the messages to all the subscribers registered to the topic by  using client.getBasicRemote().sendObject(dataFileWriter). Whenever a message is received by the subscriber, Websocket OnMessage event will be triggered at the subscriber indicating message is received. Here the message received at the subscriber end will be deserialized from receive buffer.
  
#### Load Balancing:

  If the EventBus is OverLoaded by the message i.e. whenever the message limit crosses a certain threshold set at the eventbus end, it broadcast the message to clients to switch the eventbus.
Message with TypeCode ‘003’ and the new port number of neighbor eventbus to which it has to connect will be sent indicating the eventbus change event.

  Once clients receive the Message of Typecode ‘003’ the connection with the existing eventbus will be closed and a new connection to neighbor eventbus will be established thereby switching to a new eventbus and all the messages which has to be published will be then published to this new eventbus. Thus scalability among the event buses is achieved in order to load balance the messages.


