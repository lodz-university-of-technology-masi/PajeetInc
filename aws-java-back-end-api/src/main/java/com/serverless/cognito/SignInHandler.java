package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SignInHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(SignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);
            LOG.info(body);
            /*
            {
                "email": "example@example.com",
                "password": "!Password123"
            }
             */

            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
            authRequest.setAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH);
            authRequest.setClientId(cognitoConfig.getClientId());
            authRequest.setUserPoolId(cognitoConfig.getUserPoolId());
            authRequest.addAuthParametersEntry("USERNAME", body.get("email").asText());
            authRequest.addAuthParametersEntry("PASSWORD", body.get("password").asText());

            try {
                AdminInitiateAuthResult authResult = cognitoClient.adminInitiateAuth(authRequest);

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(authResult.getAuthenticationResult())
                        .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                        .build();
            } catch (NotAuthorizedException ex) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(ex.getStatusCode())
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                        .build();
            }
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            Response responseBody = new Response("Error in processing input request: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                    .build();
        }
    }
}
