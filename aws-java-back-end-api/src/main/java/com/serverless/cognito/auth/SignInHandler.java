package com.serverless.cognito.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.cognito.CognitoConfig;
import com.serverless.cognito.UserManagement;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SignInHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(SignInHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);

            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
            authRequest.setAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH);
            authRequest.setClientId(cognitoConfig.getClientId());
            authRequest.setUserPoolId(cognitoConfig.getUserPoolId());
            authRequest.addAuthParametersEntry("USERNAME", body.get("email").asText());
            authRequest.addAuthParametersEntry("PASSWORD", body.get("password").asText());

            try {
                AdminInitiateAuthResult authResult = cognitoClient.adminInitiateAuth(authRequest);

                if (authResult.getChallengeName() != null &&
                        authResult.getChallengeName().equals("NEW_PASSWORD_REQUIRED")) {
                    AdminAddUserToGroupRequest addRequest = new AdminAddUserToGroupRequest();
                    addRequest.setUserPoolId(cognitoConfig.getUserPoolId());
                    addRequest.setUsername(body.get("email").asText());
                    addRequest.setGroupName("Candidates");
                    AdminAddUserToGroupResult addResult = cognitoClient.adminAddUserToGroup(addRequest);

                    return ApiGatewayResponse.builder()
                            .setStatusCode(200)
                            .setObjectBody(authResult)
                            .build();
                }
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(authResult.getAuthenticationResult())
                        .build();
            } catch (NotAuthorizedException ex) {
                LOG.error(ex);
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
