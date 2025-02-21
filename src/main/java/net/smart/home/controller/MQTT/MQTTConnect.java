package net.smart.home.controller.MQTT;

import java.util.Map;
import java.util.UUID;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.smart.home.controller.API.AwsSecretsRetriever;
import net.smart.home.controller.API.AwsSecretsSaveToFile;
import net.smart.home.controller.Utility.AWSIotTLSUtils;

//Purpose of this java object is to create a client and establish a connection with the aws iot core mqtt topic
@Service
public class MQTTConnect implements MQTTConnectInterface{
    private static final Logger log = LoggerFactory.getLogger(MQTTConnect.class);
    private final AwsSecretsRetriever awsSecretsRetriever;
    private MqttClient client;
    private AwsSecretsSaveToFile awsSecretsSaveToFile;
    private final AWSIotTLSUtils awsIotTLSUtils;

    public MQTTConnect(AwsSecretsRetriever awsSecretsRetriever, AwsSecretsSaveToFile awsSecretsSaveToFile, AWSIotTLSUtils awsIotTLSUtils){
        this.awsSecretsRetriever = awsSecretsRetriever;
        this.awsSecretsSaveToFile = awsSecretsSaveToFile;
        this.awsIotTLSUtils = awsIotTLSUtils;
    }

    @Override
    public void init(String brokerUrl, String secretName, String rootCaPath){
        try{
            //Fetch AWS Secrets Manager Json
            log.info("Attempting to Fetch AWS Credentials from AWS Secrets Manager...");
            String secretJson = awsSecretsRetriever.getSecret(secretName);
            if (secretJson == null || secretJson.isEmpty()){
                log.error("Error occured while attempting to get AWS Secrets From AWS Secrets Manager :( Line 35 MQTTConnect.java");
                throw new RuntimeException("Unable to Retrieve certificates from AWS Secrets Manager Line 35 MQTTConnect.java");
            } else {
                log.info("Successfully Retrieved Secrets from Secrets Manager!");
            }

            //Save the cert file temporarily
            log.info("Attmepting to Save the Certificate Information and Get File Paths from AWS Secrets Maager...");
            Map<String, String> filePaths = awsSecretsSaveToFile.saveToFile(secretJson);
            String certFilePath = filePaths.get("certFile");
            String keyFilePath = filePaths.get("keyFile");
            log.info("The Secrets from AWS Secrets Manager have successfully been saved.");

            //Set up Client
            log.info("The broker we are looking to connect to is: " + brokerUrl);
            log.info("Attempting to Construct clientId...");
            String clientId = "EC2-SmartHome-" + UUID.randomUUID();
            log.info("The clientID Constructed is: " + clientId);
            log.info("Attempting to build the client...");
            client = new MqttClient("ssl://" + brokerUrl + ":8883", clientId, new MemoryPersistence());
            log.info("The value of the client is: " + client);

            //Construct Connection of the client(This app) to the MQTT Topic
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);
            options.setSocketFactory(AWSIotTLSUtils.getSocketFactory(certFilePath, keyFilePath, rootCaPath));

            //Make the Connection
            log.info("Attempting to connect to AWS IOT Core at the broker of " + brokerUrl);
            client.connect(options);
            log.info("Successfully connected to AWS IOT Core at broker" + brokerUrl);

        } catch (MqttException e){
            log.error("Error occured at MQTTConnect.java line 51");
            log.error("❌ MQTT Exception while connecting to AWS IoT Core: {}", e.getMessage(), e);
            log.error("❌ Reason Code: {}", e.getReasonCode());
            log.error("❌ Cause: {}", e.getCause());
            log.error("Error occured while attempting to connect to AWS IOT Core broker Line 39 MQTTConnect.java", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error occured at MQTTConnect.java line 57");
            log.error("❌ Unexpected Exception while connecting to AWS IoT Core!", e.getMessage(), e);
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
        }catch (MqttException e) {
            log.error("Error occured on line 82 MQTTConnect.java");
            log.error("❌ MQTT Exception while disconnecting", e);
            log.error("❌ Reason Code: {}", e.getReasonCode());
            log.error("❌ Cause: {}", e.getCause());
        } catch (Exception e) {
            log.error("Error occured at MQTTConnect.java line 88");
            log.error("❌ Unexpected Exception while disconnecting MQTT client", e.getMessage(), e);
        }
    }
}

//Things to do next
//1. Get certificate, - new terraform code i guess we need to authenticate witha  cert even if we send messages to the topic, IAM ROles alone are not enough to authenticate us if we use MQTT(It is enough for http)
//2. Update application to use the cert when we authenticate with the topic - wioll need to inject via environment vairables, and set on
// options.setSocketFactory(AWSIotTLSUtils.getSocketFactory(
//         "/path/to/certificate.pem",
//         "/path/to/privateKey.pem",
//         "/path/to/AmazonRootCA1.pem"
//     ));

//Look up chat gpt logs we have to see the next steps as I am taking a break