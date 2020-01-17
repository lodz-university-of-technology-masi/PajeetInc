package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RemoveTestHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);
        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();

        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);
        Table tests = DynamoDbController.getTable("Tests");
        tests.deleteItem(primaryKey);
    }
}