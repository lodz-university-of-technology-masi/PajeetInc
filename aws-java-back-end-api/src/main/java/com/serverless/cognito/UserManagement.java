package com.serverless.cognito;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

public class UserManagement {
    public AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {
        CognitoConfig cognitoConfig = new CognitoConfig();

        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(cognitoConfig.getRegion())
                .build();

    }
}
