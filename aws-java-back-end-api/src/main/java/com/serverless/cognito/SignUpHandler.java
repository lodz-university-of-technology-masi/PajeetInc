package com.serverless.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public class SignUpHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(SignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    private AdminAddUserToGroupResult addUserToGroup(String email, String group) {
        try {
            AdminAddUserToGroupRequest addRequest = new AdminAddUserToGroupRequest();
            addRequest.setUserPoolId(cognitoConfig.getUserPoolId());
            addRequest.setUsername(email);
            addRequest.setGroupName(group);

            AdminAddUserToGroupResult addResult = cognitoClient.adminAddUserToGroup(addRequest);
            return addResult;
        } catch(Exception ex) {
            LOG.error("Error in adding user to a group " + ex);
        }
        return null;
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);

            /* request json
            {
                "email": "example@example.com",
                "password": "!Password123",
                "profile": "Candidate"
            }
            */

            SignUpRequest signUpRequest = new SignUpRequest();
            signUpRequest.setClientId(cognitoConfig.getClientId());
            signUpRequest.setUsername((String)input.get("email"));
            signUpRequest.setPassword((String)input.get("password"));

            List<AttributeType> cognitoAttrs = new LinkedList<>();
            cognitoAttrs.add(new AttributeType()
                    .withName("profile")
                    .withValue((String)input.get("profile")));
            signUpRequest.setUserAttributes(cognitoAttrs);

            SignUpResult signUpResult = cognitoClient.signUp(signUpRequest);

            String group = "";
            if ((input.get("profile")).equals("Candidate")) {
                group = "Candidates";
                LOG.info(addUserToGroup((String) input.get("email"), group));
            }
            else if ((input.get("profile")).equals("Recruiter")) {
                group = "Recruiters";
                LOG.info(addUserToGroup((String) input.get("email"), group));
            }

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(signUpResult)
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
