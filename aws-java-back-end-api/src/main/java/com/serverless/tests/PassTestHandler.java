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

public class PassTestHandler implements RequestStreamHandler {

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
        String answers = getAnswersWithClosedAnswersRatedAsJson(rootNode.get("answers"), test);
        int points = calculatePoints(answers);
        boolean passed = isPassed(points, test.getInt("min_points"));
        boolean finished = true;
        boolean rated = false;

        String result = JsonFormatter.getCandidatesAsJsonString(username, answers, passed, finished, rated, points, test);
        return result;
    }

    private String getAnswersWithClosedAnswersRatedAsJson(JsonNode answers, Item test) throws IOException {
        List<JsonNode> testQuestions =
                iteratorToList(
                        new ObjectMapper().readValue(test.getJSONPretty("questions"), JsonNode.class).iterator()
                );

        String json = "[";
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            boolean correct = false;
            boolean rated = false;
            String type = answer.get("type").asText();
            if (
                    type.contains("W") || type.contains("L")
            ) {
                JsonNode testQuestion = testQuestions.stream().filter(
                        q -> q.get("question_content").asText().contains(
                                answer.get("question").asText()
                        )
                ).findFirst().get();

                if (type.contains("W")) {
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
                } else if (type.contains("L")) {
                    Double testCorrectAnswer = testQuestion.get("correct_answer").asDouble();
                    if (answer.get("content").asDouble() == testCorrectAnswer) {
                        correct = true;
                        rated = true;
                    }
                }
            }
            json += JsonFormatter.getCandidateAnswerAsJsonString(
                    answer.get("question").asText(),
                    answer.get("type").asText(),
                    answer.get("content").asText(),
                    correct,
                    rated
            );
            json += i != answers.size() - 1 ? "," : "";
        }
        json += "]";
        return json;
    }

    private int calculatePoints(String json) throws IOException {
        int points = 0;
        List<JsonNode> answers = iteratorToList(new ObjectMapper().readValue(json, JsonNode.class).iterator());
        for (int i = 0; i < answers.size(); i++) {
            String type = answers.get(i).get("type").asText();
            if (
                    (
                            type.contains("W") || type.contains("L")
                    ) &&
                            answers.get(i).get("correct").asBoolean()

            ) {
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