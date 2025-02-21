package net.smart.home.controller.API;

public interface AwsSecretsRetrieverInterface {
    String getSecret(String secretsName);
}
