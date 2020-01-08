package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ChangePasswordHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ConfirmSignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);

            /*
            {
                "email": "kpm14005@eveav.com",
                "old_password": "!Password123",
                "new_password": "Password123!"
            }
            */

            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
            authRequest.setAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH);
            authRequest.setClientId(cognitoConfig.getClientId());
            authRequest.setUserPoolId(cognitoConfig.getUserPoolId());
            authRequest.addAuthParametersEntry("USERNAME", (String)input.get("email"));
            authRequest.addAuthParametersEntry("PASSWORD", (String)input.get("old_password"));

            AdminInitiateAuthResult authResult = cognitoClient.adminInitiateAuth(authRequest);

            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setAccessToken(authResult.getAuthenticationResult().getAccessToken());
            changePasswordRequest.setPreviousPassword((String)input.get("old_password"));
            changePasswordRequest.setProposedPassword((String)input.get("new_password"));


            ChangePasswordResult changePasswordResult = cognitoClient.changePassword(changePasswordRequest);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(changePasswordResult)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            Response responseBody = new Response("Error in retrieving user items: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
