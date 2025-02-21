package net.smart.home.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import net.smart.home.controller.MQTT.MQTTConnect;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "aws.iot.brokerendpoint=tcp://test-broker:1883")
class SmartHomeControllerApplicationTests {


	@Mock
	private MQTTConnect mqttConnect;

	@InjectMocks
	private SmartHomeControllerApplication smartHomeControllerApplication;

	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void contextLoads() {
		
	}




}
