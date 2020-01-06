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


public class ConfirmSignUpHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private static final Logger LOG = LogManager.getLogger(ConfirmSignUpHandler.class);
    private static final CognitoConfig cognitoConfig = new CognitoConfig();
    private static final AWSCognitoIdentityProvider cognitoClient = new UserManagement()
            .getAmazonCognitoIdentityClient();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            LOG.info(input);

            /*
            {
                "email": "kpm14005@eveav.com",
                "confirmation_code": "928205"
            }
            */

            ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
            confirmSignUpRequest.setClientId(cognitoConfig.getClientId());
            confirmSignUpRequest.setUsername((String)input.get("email"));
            confirmSignUpRequest.setConfirmationCode((String)input.get("confirmation_code"));

            ConfirmSignUpResult confirmSignUpResult = cognitoClient.confirmSignUp(confirmSignUpRequest);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(confirmSignUpResult)
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
