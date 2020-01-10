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
import java.util.Iterator;

public class AssignCandidateHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);

        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();
        String testName = rootNode.get("testName").asText();

        Table tests = DynamoDbController.getTable("Tests");

        PrimaryKey primaryKey = new PrimaryKey("recruiter_id", recruiterId, "test_id", testId);
        Item test = DynamoDbController.getItemByPrimaryKey(primaryKey, tests);

        String candidates = updateCandidates(rootNode, test);
        DynamoDbController.updateCandidates(primaryKey, candidates, tests);
    }

    private String updateCandidates(JsonNode rootNode, Item test) throws IOException {
        String username = rootNode.get("username").asText();

        String result = "[";
        Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
        while (candidates.hasNext()) {
            JsonNode candidate = candidates.next();
            if (!candidate.get("username").asText().contentEquals(username)) {
                result += JsonFormatter.getCandidateAsJsonString(candidate);
                result += ",";
            } else {
                // exception
            }
        }
        result +=  JsonFormatter.getCandidateAsJsonString(username, "[]", false, false, false, 0);
        result += "]";
        return result;
    }
}