package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ConfirmForgotPassword implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ConfirmSignUpHandler.class);
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
                "email": "kpm14005@eveav.com",
                "confirmation_code": 213213,
                "password": "!Password123"
            }
            */

            try {
                ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest();
                confirmForgotPasswordRequest.setClientId(cognitoConfig.getClientId());
                confirmForgotPasswordRequest.setUsername(body.get("email").asText());
                confirmForgotPasswordRequest.setConfirmationCode(body.get("confirmation_code").asText());
                confirmForgotPasswordRequest.setPassword(body.get("password").asText());
                ConfirmForgotPasswordResult confirmForgotPasswordResult =
                        cognitoClient.confirmForgotPassword(confirmForgotPasswordRequest);


                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(confirmForgotPasswordResult)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            } catch (NotAuthorizedException ex) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(ex.getStatusCode())
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            Response responseBody = new Response("Error in processing input request: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
