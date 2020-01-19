package com.serverless.cognito.candidates;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.cognito.CognitoConfig;
import com.serverless.cognito.UserManagement;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class ListCandidatesHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ListCandidatesHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private List<UserType> users;

    private String GetBatchOfUsers(String paginationToken)
    {
        ListUsersInGroupRequest listCandidatesRequest = new ListUsersInGroupRequest();
        listCandidatesRequest.setUserPoolId(cognitoConfig.getUserPoolId());
        listCandidatesRequest.setGroupName("Candidates");
        if (paginationToken != null)
            listCandidatesRequest.setNextToken(paginationToken);
        listCandidatesRequest.setLimit(60);

        ListUsersInGroupResult listCandidatesResult = cognitoClient.listUsersInGroup(listCandidatesRequest);
        users.addAll(listCandidatesResult.getUsers());

        return listCandidatesResult.getNextToken();
    }


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            users = new ArrayList<>();
            String paginationToken = null;

            do {
                paginationToken = GetBatchOfUsers(paginationToken);
            } while(paginationToken != null);

            String jsonString = objectMapper.writeValueAsString(users);
            JsonNode node = objectMapper.readValue(jsonString, JsonNode.class);
            LOG.info("Users number: " + users.size());

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(node)
                    .build();
        } catch (Exception ex) {
            LOG.error("Error in processing input request: " + ex);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(new Response("Error in retrieving user items: ", input))
                    .build();
        }
    }
}
