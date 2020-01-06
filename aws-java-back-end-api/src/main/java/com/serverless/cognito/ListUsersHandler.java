package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ListUsersHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(SignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            //todo - use getPaginationToken() and setPaginationToken() to get all the users if there are
            //more than 60 (this is a max limit per listusersRequest)

            ListUsersRequest listUsersRequest = new ListUsersRequest();
            listUsersRequest.setUserPoolId(cognitoConfig.getUserPoolId());

            List<String> attrToGet = new ArrayList<>();
            attrToGet.add("email");
            attrToGet.add("profile");
            listUsersRequest.setAttributesToGet(attrToGet);

            ListUsersResult listUsersResult = cognitoClient.listUsers(listUsersRequest);
            String jsonString = objectMapper.writeValueAsString(listUsersResult.getUsers());
            JsonNode node = objectMapper.readValue(jsonString, JsonNode.class);
//            LOG.info(node);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(node)
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
