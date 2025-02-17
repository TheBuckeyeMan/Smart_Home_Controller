package net.smart.home.controller.MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;

public interface MQTTConnectInterface {
    void init(String brokerUrl);

    boolean isConnected();

    MqttClient getClient();

    void cleanUp();
}
