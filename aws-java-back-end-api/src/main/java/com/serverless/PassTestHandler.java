package com.serverless;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class PassTestHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);

        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();
        String testName = rootNode.get("testName").asText();

        Table tests = DynamoDbController.getTable("Tests");
        PrimaryKey primaryKey = new PrimaryKey("recruiter_id", recruiterId, "test_id", testId);

        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(primaryKey);
        Item test = tests.getItem(spec);

        System.out.println(test.toJSONPretty());

        String candidates = updateCandidates(rootNode, test);
        System.out.println(candidates);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(primaryKey)
                .withUpdateExpression("set candidates=:c")
                .withValueMap(new ValueMap().withJSON(":c", candidates));
        tests.updateItem(updateItemSpec);
    }

    private String getAnswersAsJson(JsonNode answers) {
        String json = "[";
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            json += "{" +
                    "\"type\": \"" + answer.get("type").asText() + "\"," +
                    "\"content\": \"" + answer.get("content").asText() + "\"," +
                    "\"correct\":" + answer.get("correct").asBoolean() +
                    "}";
            json += i != answers.size() - 1 ? "," : "";
        }
        json += "]";
        return json;
    }

    private int calculateClosedQuestionsPoints(JsonNode answers) {
        int points = 0;
        for (JsonNode answer : answers) {
            String type = answer.get("type").asText();
            Boolean correct = answer.get("correct").asBoolean();
            if (type.contains("W") && correct.equals(true)) {
                points += 1;
            }
        }
        return points;
    }

    private String updateCandidates(JsonNode rootNode, Item test) throws IOException {
        String username = rootNode.get("username").asText();
        String answers = getAnswersAsJson(rootNode.get("answers"));
        int points = calculateClosedQuestionsPoints(rootNode.get("answers"));
        boolean passed = points >= test.getInt("min_points") ? true : false;

        String result = "[";
//        JsonNode candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class);
        Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
        while (candidates.hasNext()) {
            JsonNode candidate = candidates.next();
            if (!candidate.get("username").asText().contains(username)) {
                String answersAsText = getAnswersAsJson(candidate.get("answers"));
                result += "{" +
                        "\"username\":\"" + candidate.get("username").asText() + "\"," +
                        "\"answers\":" + answersAsText + "," +
                        "\"passed\":" + candidate.get("passed").asBoolean() + "," +
                        "\"finished\":" + candidate.get("finished").asBoolean() + "," +
                        "\"points\":" + Integer.toString(candidate.get("points").asInt()) +
                        "}";
            } else {
                result += "{" +
                        "\"username\":\"" + username + "\"," +
                        "\"answers\":" + answers + "," +
                        "\"passed\":" + passed + "," +
                        "\"finished\":" + "true" + "," +
                        "\"points\":" + points +
                        "}";
            }
            result += candidates.hasNext() == true ? "," : "";
        }
        result += "]";
        return result;
    }
}