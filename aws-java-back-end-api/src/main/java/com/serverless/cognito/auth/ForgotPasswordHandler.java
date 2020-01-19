package com.serverless.cognito.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
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


public class ForgotPasswordHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ForgotPasswordHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);

            try {
                ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
                forgotPasswordRequest.setClientId(cognitoConfig.getClientId());
                forgotPasswordRequest.setUsername(body.get("email").asText());

                ForgotPasswordResult forgotPasswordResult = cognitoClient.forgotPassword(forgotPasswordRequest);

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(forgotPasswordResult)
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
