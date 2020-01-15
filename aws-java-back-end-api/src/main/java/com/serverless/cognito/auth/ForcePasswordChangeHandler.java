package com.serverless.cognito.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.*;
import com.serverless.cognito.CognitoConfig;
import com.serverless.cognito.UserManagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ForcePasswordChangeHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ForcePasswordChangeHandler.class);
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
                "password": "!Password123",
                "token": "aSDASDASDASDASDASD"
            }
            */

            try {
                AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
                request.setUserPoolId(cognitoConfig.getUserPoolId());
                request.setClientId(cognitoConfig.getClientId());
                request.setChallengeName("NEW_PASSWORD_REQUIRED");
                Map<String,String> attr = new HashMap<String,String>();
                attr.put("NEW_PASSWORD", body.get("password").asText());
                attr.put("USERNAME", body.get("email").asText());
                request.setChallengeResponses(attr);
                request.setSession(body.get("token").asText());


                AdminRespondToAuthChallengeResult result =
                        cognitoClient.adminRespondToAuthChallenge(request);


                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(result)
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
            Response responseBody = new Response("Error in processing input request: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Access-Control-Allow-Origin", "*"))
                    .build();
        }
    }
}
