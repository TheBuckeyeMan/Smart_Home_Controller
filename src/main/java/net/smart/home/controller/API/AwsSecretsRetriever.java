package net.smart.home.controller.API;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

@Service
public class AwsSecretsRetriever implements AwsSecretsRetrieverInterface{
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsRetriever.class);

    //Orchestrator Method
    @Override
    public String getSecret(String secretsName){

        //Build the Client
        SecretsManagerClient awsClient = createSecretsManagerClient();

        //Make the Request
        GetSecretValueRequest awsRequest = makeAwsSecretsMangerRequest(secretsName);

        //Capture the response
        String awsResponse = getSecretsManagerResponse(awsClient, awsRequest);
        return awsResponse;
    }
    
    //Private Method to Create Secrets Manager Client
    private SecretsManagerClient createSecretsManagerClient(){
        try{
            log.info("Attempting to Create the Client for AWS Secrets Manager");
            SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
            log.info("The Secrets Manager Client built Successfully");
            return client;
        } catch (SecretsManagerException e) {
            log.error("A SecretsManagerException Error occured while attempting to build the aws Secrets Manager Client ", e.getMessage(), e);
        } catch (Exception e) {
            log.error("A Exception Error occured while attempting to build the aws Secrets Manager Client ", e.getMessage(), e);
        }
        return null;
    }

    //Private Method to Send the request to get the SecretsManager value
    private GetSecretValueRequest makeAwsSecretsMangerRequest(String secretsName){
        try{
        log.info("Attempting to make the request to AWS Secrets Manager for the secret of: " + secretsName);
        GetSecretValueRequest request = GetSecretValueRequest.builder()
            .secretId(secretsName)
            .build();
        log.info("Successfully retrieved the secret for the value of: " + secretsName);
        return request;

        } catch (IllegalArgumentException e) {
            log.error("An IllegalArgumentException occured in the value of secretsName: " + secretsName + " In MQTTCOnnect.java line 127", e.getMessage(), e);
        } catch (Exception e) {
            log.error("An Exception occured In MQTTCOnnect.java line 129", e.getMessage(), e);
        }
        return null;
    }

    //Private Method to capture the secrets manager value
    private String getSecretsManagerResponse(SecretsManagerClient awsClient, GetSecretValueRequest awsRequest){
        try{
            log.info("Attempting to Capture the response of the AWS Secrets manager");
            GetSecretValueResponse response = awsClient.getSecretValue(awsRequest);
            log.info("The value was captured successfully!");
            return response.secretString();
        } catch (Exception e) {
            log.error("An Error occured while trying to capture the response from AWS Secrets Manager", e.getMessage(), e);
        }
        return null;
    }
}
