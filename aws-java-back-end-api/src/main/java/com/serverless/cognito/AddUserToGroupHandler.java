package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jdk.internal.jline.internal.Log;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class AddUserToGroupHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(AddUserToGroupHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);

            /*  groups - Recruiters/Candidates - set a role (unauthorized users cannot assign to recruiters)
                think about moving this to signUpHandler
            {
                "email": "kpm14005@eveav.com",
                "group": "Recruiters"
            }
            */

            LOG.info((String)input.get("email"));
            LOG.info((String)input.get("group"));

            AdminAddUserToGroupRequest addRequest = new AdminAddUserToGroupRequest();
            addRequest.setUserPoolId(cognitoConfig.getUserPoolId());
            addRequest.setUsername((String)input.get("email"));
            addRequest.setGroupName((String)input.get("group"));

            AdminAddUserToGroupResult addResult = cognitoClient.adminAddUserToGroup(addRequest);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(addResult)
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
