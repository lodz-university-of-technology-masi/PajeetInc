package com.serverless;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.serverless.dynamodbmodel.UserInfoTable;
import com.serverless.dynamodbmodel.UserInfoItem;


public class AddUserInfoHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(AddUserInfoHandler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {
			JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
			UserInfoTable table = new UserInfoTable();
			UserInfoItem item = new UserInfoItem();
			item.setId(body.get("id").asText());
			item.setUsername(body.get("username").asText());
			item.setProfile(body.get("profile").asText());
			table.save(item);

			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(item)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		} catch (IOException e) {
			Response responseBody = new Response("Couldnt add new userInfo item to table.", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
					.build();
		}
	}
}
