package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RemoveCandidateHandler implements RequestStreamHandler {
    private static final Logger LOG = LogManager.getLogger(RemoveCandidateHandler.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);
        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();
        String username = rootNode.get("username").asText();

        Table tests = DynamoDbController.getTable("Tests");
        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);
        Item test = DynamoDbController.getItemByPrimaryKey(primaryKey, tests);

        String candidates = JsonFormatter.removeCandidateFromTestByUsername(test, username);
        DynamoDbController.updateCandidates(primaryKey, candidates, tests);
    }
}
