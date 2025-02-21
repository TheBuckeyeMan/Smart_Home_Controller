package net.smart.home.controller.API;

import java.util.Map;

public interface AwsSecretsSaveToFileInterface {
    Map<String, String> saveToFile(String secretJson);

} 