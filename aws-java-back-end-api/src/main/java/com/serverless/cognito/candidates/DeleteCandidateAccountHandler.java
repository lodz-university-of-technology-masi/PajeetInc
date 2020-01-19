package com.serverless.cognito.candidates;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
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


public class DeleteCandidateAccountHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(DeleteCandidateAccountHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            Map<String, String> pathParameters = (Map<String,String>)input.get("pathParameters");

            try {
                AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest();
                adminDeleteUserRequest.setUserPoolId(cognitoConfig.getUserPoolId());
                adminDeleteUserRequest.setUsername(pathParameters.get("email"));

                AdminDeleteUserResult adminDeleteUserResult = cognitoClient.adminDeleteUser(adminDeleteUserRequest);

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(adminDeleteUserResult)
                        .build();
            } catch (NotAuthorizedException ex) {
                LOG.error("Error in processing input request: " + ex);
                return ApiGatewayResponse.builder()
                        .setStatusCode(ex.getStatusCode())
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .build();
            }
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(new Response("Error in retrieving user items: ", input))
                    .build();
        }
    }
}
