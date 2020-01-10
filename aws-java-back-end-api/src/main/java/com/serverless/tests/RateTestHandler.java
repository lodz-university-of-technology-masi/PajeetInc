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
import java.util.Iterator;
import java.util.List;

public class RateTestHandler implements RequestStreamHandler {

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
        String answers = JsonFormatter.getCandidateAnswersAsJsonString(rootNode.get("answers"));
        int points = calculatePoints(answers);
        boolean passed = isPassed(points, test.getInt("min_points"));
        boolean finished = true;
        boolean rated = true;

        String result = JsonFormatter.getCandidatesAsJsonString(username, answers, passed, finished, rated, points, test);
        return result;
    }

    private int calculatePoints(String json) throws IOException {
        int points = 0;
        List<JsonNode> answers = iteratorToList(new ObjectMapper().readValue(json, JsonNode.class).iterator());
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).get("correct").asBoolean()) {
                points += 1;
            }
        }
        return points;
    }

    private boolean isPassed(int points, int minPoints) {
        boolean passed = points >= minPoints ? true : false;
        return passed;
    }

    private List<JsonNode> iteratorToList(Iterator<JsonNode> iterator) {
        List<JsonNode> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }
}