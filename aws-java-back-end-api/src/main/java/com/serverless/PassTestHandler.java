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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        String candidates = updateCandidates(rootNode, test);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(primaryKey)
                .withUpdateExpression("set candidates=:c")
                .withValueMap(new ValueMap().withJSON(":c", candidates));
        tests.updateItem(updateItemSpec);
    }

    private String updateCandidates(JsonNode rootNode, Item test) throws IOException {
        String username = rootNode.get("username").asText();
        String answers = getAnswersWithClosedAnswersRatedAsJson(rootNode.get("answers"), test);
        int points;

        String result = "[";
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
                        "\"points\":" + candidate.get("points").asInt() +
                        "}";
            } else {
                result += "{" +
                        "\"username\":\"" + username + "\"," +
                        "\"answers\":" + answers + "," +
                        "\"passed\":" + "false" + "," +
                        "\"finished\":" + "true" + "," +
                        "\"points\":" + "0" +
                        "}";
            }
            result += candidates.hasNext() == true ? "," : "";
        }
        result += "]";
        return result;
    }

    private String getAnswersWithClosedAnswersRatedAsJson(JsonNode answers, Item test) throws IOException {
        List<JsonNode> questions =
                iteratorToList(
                        new ObjectMapper().readValue(test.getJSONPretty("questions"), JsonNode.class).iterator()
                );

        String json = "[";
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            boolean correct = false;
            boolean rated = false;
            if (answer.get("type").asText().contains("W")) {
                JsonNode testQuestion = questions.stream().filter(
                        q -> q.get("question_content").asText().contains(
                                answer.get("question").asText()
                        )
                ).findFirst().get();

                JsonNode testAnswers = testQuestion.get("answers");
                for (int j = 0; j < testAnswers.size(); j++) {
                    JsonNode testAnswer = testAnswers.get(j);
                    if (testAnswer.get("answer").asText()
                            .contains(answer.get("content").asText())) {
                        correct = testAnswer.get("correct").asBoolean();
                        rated = true;
                        break;
                    }
                }
            }
            json += "{" +
                    "\"type\": \"" + answer.get("type").asText() + "\"," +
                    "\"content\": \"" + answer.get("content").asText() + "\"" +
                    "\"correct\": \"" + correct + "\"" +
                    "\"rated\": \"" + rated + "\"" +
                    "}";
            json += i != answers.size() - 1 ? "," : "";
        }
        json += "]";
        return json;
    }

    private List<JsonNode> iteratorToList(Iterator<JsonNode> iterator) {
        List<JsonNode> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }

    private String getAnswersAsJson(JsonNode answers) {
        String json = "[";
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            json += "{" +
                    "\"type\": \"" + answer.get("type").asText() + "\"," +
                    "\"content\": \"" + answer.get("content").asText() + "\"" +
                    "\"correct\": \"" + answer.get("correct").asText() + "\"" +
                    "\"rated\": \"" + answer.get("rated").asText() + "\"" +
                    "}";
            json += i != answers.size() - 1 ? "," : "";
        }
        json += "]";
        return json;
    }
}