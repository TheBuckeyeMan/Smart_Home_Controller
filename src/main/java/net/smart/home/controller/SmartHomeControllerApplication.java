package net.smart.home.controller;

import java.io.File;

import org.eclipse.paho.client.mqttv3.internal.wire.MqttAck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import net.smart.home.controller.MQTT.MQTTConnect;

@SpringBootApplication
public class SmartHomeControllerApplication {
	private final MQTTConnect mqttConnect;

	public SmartHomeControllerApplication(MQTTConnect mqttConnect){
		this.mqttConnect = mqttConnect;
	}

	@Value("${aws.iot.brokerendpoint}")
    private String brokerUrl;

	@Value("${aws.secretmanager.secretname}")
    private String secretName;

	@Value("${aws.secretmanager.rootcapath}")
    private String rootCaPath;
	
	public static void main(String[] args) {
		SpringApplication.run(SmartHomeControllerApplication.class, args);
	}

	@PostConstruct
	public void initMQTT(){
		mqttConnect.init(brokerUrl, secretName, rootCaPath);
	}

}
