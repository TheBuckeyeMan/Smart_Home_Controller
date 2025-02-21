package net.smart.home.controller.MQTT;

public interface MQTTPublisherInterface {
    void sendMessage(String payload, String topic, String brokerUrl, String secretName, String rootCaPath);
}
