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
import java.util.Map;
import java.util.stream.Collectors;

public class PassTestHandler implements RequestStreamHandler {

    private List<String> jsons;

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);

        jsons = new ArrayList<>();

        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = rootNode.get("testId").asText();
        String testName = rootNode.get("testName").asText();

        Table tests = DynamoDbController.getTable("Tests");
        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);

        Item test = DynamoDbController.getItemByPrimaryKey(primaryKey, tests);

        String candidates = updateCandidates(rootNode, test);
        DynamoDbController.updateCandidates(primaryKey, candidates, tests);
    }

    private String itemsAsString() throws IOException {
        String json = new String("[");
        for (int i = 0; i < jsons.size(); i++) {
            json += i == jsons.size() - 1 ? jsons.get(i) : jsons.get(i) + ",";
        }
        json += "]";
        return json;
    }

    private String updateCandidates(JsonNode rootNode, Item test) throws IOException {
        String username = rootNode.get("username").asText();
        Map<String, List<JsonNode>> answersGroupedByQuestion = groupAnswersByQuestion(
                getJsonNodesList(rootNode.get("answers"))
        );

        rateClosedAndNumericalAnswers(answersGroupedByQuestion, test);

        String answers = itemsAsString();
        double points = calculatePoints(answers);
        boolean passed = isPassed(points, test.getInt("minPoints"));
        boolean finished = true;
        boolean rated = false;
//        boolean rated = testComprisesOnlyClosedAndNumerical();


        String result = JsonFormatter.getCandidatesAsJsonString(username, answers, passed, finished, rated, points, test);
        return result;
    }

    private void rateClosedAndNumericalAnswers(Map<String, List<JsonNode>> answersGroupedByQuestion, Item test) throws IOException {
        List<JsonNode> testQuestions = getTestQuestions(test);

        List<String> questions = new ArrayList<String>(answersGroupedByQuestion.keySet());
        for (int i = 0; i < answersGroupedByQuestion.size(); i++) {

            String question = questions.get(i);
            List<JsonNode> answers = answersGroupedByQuestion.get(question);
            if (answers.size() != 0) {

                String type = answers.get(0).get("type").asText();
                if (type.contentEquals("W")) {
                    JsonNode testQuestion = findItemByProperty(question, testQuestions);
                    Points points = calculateClosedQuestionPoints(answers, testQuestion);
                    addClosedToJson(answers, points, true);
                } else if (type.contentEquals("L")) {
                    JsonNode testQuestion = findItemByProperty(question, testQuestions);
                    JsonNode answer = answers.get(0);
                    double points = calculateNumericalQuestionPoints(answer, testQuestion);
                    addToJson(answers, points == 0.0 ? false : true, true, points);
                } else if (type.contentEquals("O")) {
                    addToJson(answers, false, false, 0.0);
                }
            }
        }
    }

    private void addClosedToJson(List<JsonNode> answers, Points points, boolean rated) {
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            jsons.add(
                    JsonFormatter.getCandidateAnswerAsJsonString(
                            answer.get("question").asText(),
                            answer.get("type").asText(),
                            answer.get("content").asText(),
                            points.getCorrects().get(i),
                            rated,
                            points.getPoints()
                    )
            );
        }
    }

    private void addToJson(List<JsonNode> answers, boolean correct, boolean rated, double points) {
        for (JsonNode a : answers) {
            jsons.add(
                    JsonFormatter.getCandidateAnswerAsJsonString(
                            a.get("question").asText(),
                            a.get("type").asText(),
                            a.get("content").asText(),
                            correct,
                            rated,
                            points
                    )
            );
        }
    }

    private Points calculateClosedQuestionPoints(List<JsonNode> answers, JsonNode testQuestion) throws IOException {
        Points p = new Points();

        //        double points = 0.0;

        List<JsonNode> testAnswers = getJsonNodesList(testQuestion.get("answers"));

        List<Boolean> corrects = new ArrayList<>();
        // wszystkie zaznaczone
        if (testAnswers.size() == answers.size()) {
            p = new Points(0.0, corrects);
        }
        // zaznaczone mniej niz wwszystkie, wiecej niz 0 // nic nie zaznaczone sprawdzane jest wyzej
        else {

            int allGood = findAllGood(testAnswers);
            int good = 0;
            int wrong = 0;
            for (JsonNode a : answers) {

                JsonNode testAnswer = findTestAnswerByContent(a.get("content").asText(), testAnswers);
                if (testAnswer.get("correct").asBoolean()) {
                    good++;
                } else {
                    wrong++;
                }
                corrects.add(testAnswer.get("correct").asBoolean());
            }
            double maxPointsPerQuestion = testQuestion.get("points").asDouble();
            double points = calculateByGoodAndWrong(good, wrong, maxPointsPerQuestion, allGood);
            double pointsPerAnswer = points / (answers.size() * 1.0);
            p = new Points(pointsPerAnswer, corrects);
        }

        return p;
    }

    private double calculateNumericalQuestionPoints(JsonNode answer, JsonNode testQuestion) throws IOException {
        double points = 0.0;
        double correctAnswer = testQuestion.get("correct").asDouble();
        if (correctAnswer == answer.get("content").asDouble()) {
            points = testQuestion.get("points").asDouble();
        }
        return points;
    }

    private double calculateByGoodAndWrong(int good, int wrong, double points, int allGood) {
        double result = points;
        if (good == 0) {
            result = 0.0;
        } else if (good > 0) {
            if (good == allGood) {
                if (wrong == 0) {
                    result = points;
                } else if (wrong > 0) {
                    if (good == wrong) {
                        result = 0.0;
                    } else if (good > wrong && wrong == 1) {
                        result = points * 0.33;
                    } else if (good > wrong && wrong > 1) {
                        result = 0.0;
                    }
                }
            } else if (good < allGood) {
                if (wrong == 0) {
                    result = points * 0.66;
                } else if (wrong > 0) {
                    result = 0.0;
                }
            }
        }
        return result;
    }

    private double calculatePoints(String json) throws IOException {
        double points = 0.0;
        List<JsonNode> answers = iteratorToList(new ObjectMapper().readValue(json, JsonNode.class).iterator());


        for (int i = 0; i < answers.size(); i++) {
            String type = answers.get(i).get("type").asText();
            if (
                    (
                            type.contentEquals("W") || type.contentEquals("L")
                    ) &&
                            answers.get(i).get("correct").asBoolean()

            ) {
                points += answers.get(i).get("points").asDouble();
            }
        }
        return points;
    }

    private List<JsonNode> getJsonNodesList(JsonNode node) {
        List<JsonNode> nodes = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            nodes.add(node.get(i));
        }
        return nodes;
    }

    private Map<String, List<JsonNode>> groupAnswersByQuestion(List<JsonNode> answers) {
        return answers.stream().collect(Collectors.groupingBy(a -> a.get("question").asText()));
    }

    private List<JsonNode> getTestQuestions(Item test) throws IOException {
        return iteratorToList(
                new ObjectMapper().readValue(test.getJSONPretty("questions"), JsonNode.class).iterator()
        );
    }

    private JsonNode findTestAnswerByContent(String property, List<JsonNode> items) {
        return items
                .stream()
                .filter(
                        tq -> tq.get("answer").asText().contentEquals(property))
                .findFirst()
                .get();
    }

    private JsonNode findItemByProperty(String property, List<JsonNode> items) {
        return items
                .stream()
                .filter(
                        tq -> tq.get("content").asText().contentEquals(property))
                .findFirst()
                .get();
    }

    private int findAllGood(List<JsonNode> testAnswers) {
        return testAnswers
                .stream()
                .filter(
                        ta -> ta.get("correct").asBoolean())
                .collect(Collectors.toList()).size();
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