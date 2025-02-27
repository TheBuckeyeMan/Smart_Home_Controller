package net.smart.home.controller.Controller.GET;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import net.smart.home.controller.MQTT.MQTTConnect;
import net.smart.home.controller.MQTT.MQTTPublisher;

@RequestMapping("/prod")
@Slf4j
@RestController
public class GetController {
    private final MQTTPublisher mqttPublisher;
    private MQTTConnect mqttConnect;

    //Currently the broker of the IoT COre for the Smart Home
    @Value("${aws.iot.brokerendpoint}")
    private String brokerUrl;

    @Value("${aws.secretmanager.secretname}")
    private String secretName;

	@Value("${aws.secretmanager.rootcapath}")
    private String rootCaPath;

    public GetController(MQTTPublisher mqttPublisher, MQTTConnect mqttConnect){
        this.mqttPublisher = mqttPublisher;
        this.mqttConnect = mqttConnect;
    }

    @GetMapping("/reconnect")
    public void reconnectToTopic(){
        log.info("Attempting to Reconnect to Topic...");
        mqttConnect.init(brokerUrl, secretName, rootCaPath);
        log.info("Reconnect was successful!");
    }

    @GetMapping("/testec2")
    public String testEc2(){
        return "The Endpoint /testec2 was called and is functioning as expected";
    }

    @GetMapping("/testpi")
    public String testPi(){
        log.info("Attempting to Trigger Rasberi Pi Device");
        mqttPublisher.sendMessage("{ \"command\": \"test\" }", "iot/smart-home/commands", brokerUrl, secretName, rootCaPath);
        log.info("The Raspberry Pi Was Triggered successfully!");
        return "Test message sent to the topic";
    }   

    @GetMapping("/sendmessage")
    public String sendMessage(){
        String payload = "{ \"command\": \"trigger_cli\" }";
        String topic = "iot/smart-home/test";
        log.info("Attempting to send message:" + payload + " to the topic: " + topic + " At the broker of: " + brokerUrl);
        mqttPublisher.sendMessage(payload, topic, brokerUrl, secretName, rootCaPath);
        log.info("Sucessfully sent the message to the required Location at: " + topic + "and at: " + brokerUrl);
        return "Message sent to IoT Core";
    }

    @GetMapping("/triggerlights")
    public String triggerLights(){
        String payload = "{ \"command\": \"turn_on_light\" }";
        String topic = "iot/smart-home/sportslights";
        log.info("Attempting to Trigger the smart home lights!");
        mqttPublisher.sendMessage(payload, topic, brokerUrl, secretName, rootCaPath);
        log.info("Successfully Sent message to trigger lights to aws core iot");
        return "Smart home lights triggered successfully!";
    }
}