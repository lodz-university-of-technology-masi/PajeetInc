package com.serverless.cognito;

public class CognitoConfig
{
    // JK
    private String clientId = "5so65sptaaj1j45f6jjv95lqr0";
    private String userPoolId = "us-east-1_sDNtbYX1l";
    private String endpoint = "cognito-idp.us-east-1.amazonaws.com";
    private String region = "us-east-1";
    private String identityPoolId = "us-east-1:24a14ef1-6bc7-489d-9368-a87d93bf00df";

    // MW
//    private String clientId = "6cfslk28er7vknkfcbs6ibfoh6";
//    private String userPoolId = "us-east-1_zTn5RVQHl";
//    private String endpoint = "cognito-idp.us-east-1.amazonaws.com";
//    private String region = "us-east-1";
//    private String identityPoolId = "us-east-1:84c516ed-cc3d-4276-b2be-da7651a7d9d9";

    public String getClientId() {
        return clientId;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getRegion() {
        return region;
    }

    public String getIdentityPoolId() {
        return identityPoolId;
    }
}
