package com.serverless.cognito.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.Collections;
import java.util.Map;
import com.serverless.cognito.CognitoConfig;
import com.serverless.cognito.UserManagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ConfirmForgotPassword implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ConfirmForgotPassword.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);

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
                        .build();
            } catch (NotAuthorizedException ex) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(ex.getStatusCode())
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .build();
            }
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(new Response("Error in processing input request: ", input))
                    .build();
        }
    }
}
