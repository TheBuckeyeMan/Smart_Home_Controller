package net.smart.home.controller.MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;
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
        try{
            //Validate Inputs
            validateArgs(payload, topic);

            //Get the client from the connection we set up in connect, reconnect if disconnected
            MqttClient client = mqttConnect.getClient();
            if (client == null || !client.isConnected()){
                log.warn("MQTT client is not connected. Attempting reconnection...");
                mqttConnect.init(brokerUrl, secretName, rootCaPath);
                client = mqttConnect.getClient();
            }

            //Send message if we are connected
            if (client.isConnected()){
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                log.info("Message was successfully published to Topic: " + topic + " message: " + payload + " brokerUrl: " + brokerUrl);
            } else {
                log.error("Error occured while attempting to send message to MQTT Topic line 44 MQTTPublisher.java");
            }
        } catch (Exception e){
            log.error("Error occured while attempting to send message to MQTT Topic line 49 MQTTPublisher.java", e.getMessage(), e);
        }
    }

    private void validateArgs(String payload, String topic){
        if (payload == null || payload.isEmpty()){
            throw new IllegalArgumentException("The contents passed to the payload variable while attempting to send a message to the MQTT Topic was null");
        }
        if (topic == null || topic.isEmpty()){
            throw new IllegalArgumentException("The contents passed to the topic variable while attempting to send a message to the MQTT Topic was null");
        }
    }
}
