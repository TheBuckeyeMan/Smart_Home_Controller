package net.smart.home.controller.API;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AwsSecretsSaveToFile implements AwsSecretsSaveToFileInterface{
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsSaveToFile.class);

    @Override
    public Map<String, String> saveToFile(String secretJson){
        Map<String, String> filePaths = new HashMap<>();

        //Parse the Json Secret from AWS Secrets Manager to Individual Variables for their respective Secrets
        Map<String, String> secretMap = mapJson(secretJson);
        String certificate_pem = getPem(secretMap, "certificate_pem");
        String privateKeyPem = getPem(secretMap, "private_key");
        //Uncomment if needing to debug
        // log.info("The value of the Certificate from line 29 of AwsSecretsSaveToFile.java is: " + certificate_pem);
        // log.info("The value of the private key from line 30 of AwsSecretsSaveToFile.java is: " + privateKeyPem);
        
        try{
        //Write the Credentials to the files
        File certFile = writeToTempFile("cert.pem", certificate_pem);
        File keyFile = writeToTempFile("privateKey.pem", privateKeyPem);

        //Structure file path returns
        log.info("Attempting to build a Map<Stirng, String> for the filePaths return...");
        filePaths.put("certFile", certFile.getAbsolutePath());
        filePaths.put("keyFile", keyFile.getAbsolutePath());
        log.info("Successfully built a Map<Stirng, String> for the filePaths return");
        return filePaths;
        } catch (IOException e) {
            log.error("IOException occured while attempting to create the files for the credentials and write to them", e.getMessage(), e);
        }
        return filePaths;
    }

    private Map<String, String> mapJson(String secretJson){
        try{
            log.info("Attempting to map the Json AwsSecrets Object...");
            Map<String, String> secretMap = new ObjectMapper().readValue(secretJson, Map.class);
            log.info("The Json Secrets Object was Mapped Successfully!");
            return secretMap;
        } catch (JsonParseException e) {
            log.error("JsonParseException occured while parsing the Json AwsSecretsSaveToFile line 37", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error occured while parsing the Json AwsSecretsSaveToFile line 39", e.getMessage(), e);
        }
        return null;
    }

    private String getPem(Map<String, String> secretMap, String key){
        try{
        log.info("Attempting to get the " + key + " value from the secretsMap Object");
        String pemValue = secretMap.get(key);
        log.info("Successfully retrieved pem " + key + " from the secretsMap!");
        if (pemValue == null || pemValue.isEmpty()){
            log.error("Unable to retrieve the pemValue for: " + key + " from the secretsMap AwsSecretsSaveToFile line 49");
            throw new RuntimeException("Unable to retrieve the certificatePem from the secretsJson.");
        } 
        return pemValue;
        } catch (Exception e) {
            log.error("Error occured while getting the pem value from jsonMap AwsSecretsSaveToFile line 53", e.getMessage(), e);
        }
        return null;
    }

    private File writeToTempFile(String filename, String content) throws IOException{
        log.info("Attempting to create a temporary file...");
        File tempFile = File.createTempFile(filename, ".pem");
        log.info("Temporary file " + tempFile + " Created!");
        try(FileWriter writer = new FileWriter(tempFile)){
            log.info("Attempting to write to the file " + tempFile);
            writer.write(content);
            log.info("Successfully wrote to the file " + tempFile + "!");
            return tempFile;
        } catch (IOException e) {
            log.error("IOException occured while attempting to write the AWS Secrets value to the " + filename + " File. Line 76 AwsSecretsSaveToFile.java", e.getMessage(), e);
        } catch (Exception e){
            log.error("Exception occured while attempting to write the AWS Secrets value to the " + filename + " File. Line 78 AwsSecretsSaveToFile.java", e.getMessage(), e);
        }
        return tempFile;
    }
}