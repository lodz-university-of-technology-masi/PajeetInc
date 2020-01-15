package com.serverless.cognito.candidates;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.cognito.CognitoConfig;
import com.serverless.cognito.UserManagement;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class CreateCandidateAccountHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(CreateCandidateAccountHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);
            JsonNode body = new ObjectMapper().readValue((String) input.get("body"), JsonNode.class);
            LOG.info(body);

            /*
            {
                "email": "kpm14005@eveav.com",
            }
            */

            try {
                AdminCreateUserRequest adminCreateUserRequest = new AdminCreateUserRequest();
                adminCreateUserRequest.setUserPoolId(cognitoConfig.getUserPoolId());
                adminCreateUserRequest.setUsername(body.get("email").asText());

                List<AttributeType> attr = new ArrayList<AttributeType>();
                AttributeType profile = new AttributeType();
                profile.setName("profile");
                profile.setValue("Candidate");
                attr.add(profile);
                adminCreateUserRequest.setUserAttributes(attr);

                AdminCreateUserResult adminCreateUserResult = cognitoClient.adminCreateUser(adminCreateUserRequest);

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(adminCreateUserResult)
                        .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                        .build();
            } catch (NotAuthorizedException ex) {
                LOG.error("Error in processing input request: " + ex);
                return ApiGatewayResponse.builder()
                        .setStatusCode(ex.getStatusCode())
                        .setRawBody(ex.getErrorCode() + ": " + ex.getErrorMessage())
                        .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                        .build();
            }
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            Response responseBody = new Response("Error in retrieving user items: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                    .build();
        }
    }
}
