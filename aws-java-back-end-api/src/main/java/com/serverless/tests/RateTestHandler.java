package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RateTestHandler implements RequestStreamHandler {

    private static final Logger LOG = LogManager.getLogger(RateTestHandler.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);

        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();
        String testName = rootNode.get("testName").asText();

        Table tests = DynamoDbController.getTable("Tests");
        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);

        Item test = DynamoDbController.getItemByPrimaryKey(primaryKey, tests);

        String candidates = updateCandidates(rootNode, test);
        DynamoDbController.updateCandidates(primaryKey, candidates, tests);
    }

    private String updateCandidates(JsonNode rootNode, Item test) throws IOException {
        String username = rootNode.get("username").asText();
        String answers = JsonFormatter.getCandidateAnswersAsJsonString(rootNode.get("answers"));

        double points = sumPoints(answers);
        boolean passed = isPassed(points, test.getInt("minPoints"));
        boolean finished = true;
        boolean rated = true;

        String result = JsonFormatter.getCandidatesAsJsonString(username, answers, passed, finished, rated, points, test);
        return result;
    }

    private double sumPoints(String json) throws IOException {
        double points = 0.0;
        List<JsonNode> answers = iteratorToList(new ObjectMapper().readValue(json, JsonNode.class).iterator());
        for (JsonNode a : answers) {
            if (a.get("correct").asBoolean()) {
                points += a.get("points").asDouble();
            }
        }
        return points;
    }

    private boolean isPassed(double points, int minPoints) {
        boolean passed = points >= minPoints ? true : false;
        return passed;
    }

    private List<JsonNode> iteratorToList(Iterator<JsonNode> iterator) {
        List<JsonNode> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }
}