package com.serverless.cognito;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

public class UserManagement {
    private static CognitoConfig cognitoConfig = new CognitoConfig();

    public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(cognitoConfig.getRegion())
                .build();
    }
}
