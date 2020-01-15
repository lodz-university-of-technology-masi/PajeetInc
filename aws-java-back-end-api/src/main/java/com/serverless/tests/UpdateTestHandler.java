package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class UpdateTestHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);
        updateTestItem(rootNode);
    }

    private void updateTestItem(JsonNode rootNode) {
        Table tests = DynamoDbController.getTable("Tests");

        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();

        String testName = rootNode.get("testName").asText();
        double minPoints = rootNode.get("minPoints").asDouble();
        double maxPoints = rootNode.get("maxPoints").asDouble();
        String questions = JsonFormatter.getQuestionsAsJsonString(rootNode);

        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);
        DynamoDbController.updateTest(primaryKey, testName, minPoints, maxPoints, questions, tests);
    }
}