package com.serverless.cognito.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;
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


public class ConfirmSignUpHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ConfirmSignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);

            ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
            confirmSignUpRequest.setClientId(cognitoConfig.getClientId());
            confirmSignUpRequest.setUsername(body.get("email").asText());
            confirmSignUpRequest.setConfirmationCode(body.get("confirmation_code").asText());

            try {
                ConfirmSignUpResult confirmSignUpResult = cognitoClient.confirmSignUp(confirmSignUpRequest);

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setRawBody("Account has been confirmed.")
                        .build();
            } catch (ExpiredCodeException ex) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .build();
            }

        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            Response responseBody = new Response("Error in processing input request: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .build();
        }
    }
}
