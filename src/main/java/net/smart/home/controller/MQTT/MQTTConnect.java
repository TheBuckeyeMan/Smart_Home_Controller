package net.smart.home.controller.MQTT;


import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


//Purpose of this java object is to create a client and establish a connection with the aws iot core mqtt topic
@Service
public class MQTTConnect implements MQTTConnectInterface{
    private static final Logger log = LoggerFactory.getLogger(MQTTConnect.class);
    private MqttClient client;

    @Override
    public void init(String brokerUrl){
        try{
            if(client == null){
                //Set up CLient
                String clientId = "EC2-SMartHome-" + UUID.randomUUID();
                client = new MqttClient("ssl://" + brokerUrl + ":8883", clientId, new MemoryPersistence());

                //Construct Connection of the client(This app) to the MQTT Topic
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setKeepAliveInterval(60);
                options.setAutomaticReconnect(true);

                //Make the Connection
                log.info("Attempting to connect to AWS IOT Core at the broker of " + brokerUrl);
                client.connect(options);
                log.info("Successfully connected to AWS IOT Core at broker" + brokerUrl);


            }
        } catch (Exception e){
            log.error("Error occured while attempting to connect to AWS IOT Core broker Line 39 MQTTConnect.java", e.getMessage(), e);
        }
    }

    //Method to check if the client is connected
    @Override
    public boolean isConnected(){
        return client != null && client.isConnected();
    }

    //Method to check the client related information to the broker
    @Override
    public MqttClient getClient(){
        return client;
    }

    //Method to terminate the connection if and when nessasary
    @Override
    public void cleanUp(){
        try{
            if (client != null && client.isConnected()){
                client.disconnect();;
                log.info("Successfully disconnected the client form AWS IOT Core");
            }
        } catch (Exception e) {
            log.error("Error occured while attempting to disconnect the client from AWS IOT Core", e.getMessage(), e);
        }
    }
}