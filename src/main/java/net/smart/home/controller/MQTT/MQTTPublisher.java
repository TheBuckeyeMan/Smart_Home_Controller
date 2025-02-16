package net.smart.home.controller.MQTT;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MQTTPublisher {
    private static final Logger log = LoggerFactory.getLogger(MQTTPublisher.class);

    @Value("${aws.iot.brokerendpoint}")
    private String brokerUrl;

    public void sendMessage(String payLoad, String topic){
        try{
            //Validate Message Contents
            validateArgs(payLoad, topic);

            String clientId = "EC2-SpringBoot-" + UUID.randomUUID();
            MqttClient client = new MqttClient("ssl://" + brokerUrl + ":8883", clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            log.info("Attempting to COnnect to AWS IoT Core");
            client.connect(options);
            log.info("Successfully Connected to AWS IoT Core");


            MqttMessage message = new MqttMessage(payLoad.getBytes());
            message.setQos(1);
            client.publish(topic, message);

            log.info("Message Published" + payLoad);
            client.disconnect();


        } catch (Exception e) {
            log.error("Error occured while attempting to make the MQTT Request", e.getMessage(), e);
        }
    }

    private void validateArgs(String payLoad, String topic){
        if (payLoad == null || payLoad.isEmpty()) {
            throw new IllegalArgumentException("The contents passed as the payLoad is null");
        }
        if (topic == null || topic.isEmpty()){
            throw new IllegalArgumentException("The contents passed as the topic is null");
        }
    }
    



}
