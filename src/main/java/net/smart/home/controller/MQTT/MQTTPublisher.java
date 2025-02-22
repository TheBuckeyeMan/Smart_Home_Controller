package net.smart.home.controller.MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MQTTPublisher implements MQTTPublisherInterface{
    private static final Logger log = LoggerFactory.getLogger(MQTTPublisher.class);
    private final MQTTConnect mqttConnect;

    public MQTTPublisher(MQTTConnect mqttConnect){
        this.mqttConnect = mqttConnect;
    }

    @Override
    public void sendMessage(String payload, String topic, String brokerUrl, String secretName, String rootCaPath){
        log.info("Begining the process of sending the message: " + payload + " to the topic of: " + topic + "at broker: " + brokerUrl);
        try{
            //Validate Inputs
            validateArgs(payload, topic);

            //Get the client from the connection we set up in connect, reconnect if disconnected
            log.info("Attempting to get the client from the the mqttConnect class MQTTPublisher.java line 26");
            MqttClient client = mqttConnect.getClient();
            if (client == null || !client.isConnected()){
                log.warn("MQTT client is not connected. Attempting reconnection... line 29 MQTTPublisher.java");
                mqttConnect.init(brokerUrl, secretName, rootCaPath);
                client = mqttConnect.getClient();
            }
            log.info("The value of the client retrieved from MQTTConnect was " + client + " on line 33 MQTTPublisher.java");

            //Send message if we are connected
            log.info("Attempting to send the message to topic: " + topic + " from client: " + client + "line 36 MQTTPublisher.java");
            if (client.isConnected()){
                try{
                    log.info("Attempting to build ther message from the payload... line 38 MQTTPublisher.java");
                    MqttMessage message = new MqttMessage(payload.getBytes());
                    message.setQos(1);
                    log.info("Attempting to publish the message: " + message + " to the topic: " + topic + " line 41 MQTTPublisher.java");
                    client.publish(topic, message);
                    log.info("Message was successfully published to Topic: " + topic + " message: " + payload + " brokerUrl: " + brokerUrl);
                } catch (MqttException e) {
                    log.error("A MQTTException occured while attempting to Build the message or publish to the Topic: " + topic + " line 47 MQTTPublisher.java", e.getMessage(), e);
                    log.error("The cause of this error was: " + e.getCause());
                    log.error("The reason code for this error was: ", e.getReasonCode());
                }
            } else {
                log.info("The Client: " + client + " is NOT CONNECTED to the topic: " + topic + " line 52 MQTTPublisher.java");
                log.error("Error occured while attempting to send message to MQTT Topic line 53 MQTTPublisher.java");
            }
        } catch (Exception e){
            log.error("Error occured while attempting to send message to MQTT Topic line 56 MQTTPublisher.java", e.getMessage(), e);
        }
    }

    private void validateArgs(String payload, String topic){
        log.info("Begining to validate that the arguments passed are valid. Line 61 MQTTPublisher.java");
        if (payload == null || payload.isEmpty()){
            log.error("The value passed to payload: " + payload + " was blank or null");
            throw new IllegalArgumentException("The contents passed to the payload variable while attempting to send a message to the MQTT Topic was null line 64 MQTTPublisher.java");
        }
        if (topic == null || topic.isEmpty()){
            log.error("The value passed to topic: " + topic + " was blank or null");
            throw new IllegalArgumentException("The contents passed to the topic variable while attempting to send a message to the MQTT Topic was null line 68 MQTTPublisher.java");
        }
    }
}
