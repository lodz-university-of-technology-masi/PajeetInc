package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;

public class JsonFormatter {

    protected static String getCandidatesAsJsonString(
            String username, String answers, boolean passed, boolean finished, boolean rated, double points, Item test) throws IOException {
        String result = "[";
        Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
        while (candidates.hasNext()) {
            JsonNode candidate = candidates.next();
            if (!candidate.get("username").asText().contentEquals(username)) {
                result += getCandidateAsJsonString(candidate);
            } else {
                result += getCandidateAsJsonString(username, answers, passed, finished, rated, points);
            }
            result += candidates.hasNext() == true ? "," : "";
        }
        result += "]";
        return result;
    }

    protected static String getCandidateAsJsonString(JsonNode candidate) {
        String answersAsText = getCandidateAnswersAsJsonString(candidate.get("answers"));
        String result = "{" +
                "\"username\":\"" + candidate.get("username").asText() + "\"," +
                "\"answers\":" + answersAsText + "," +
                "\"passed\":" + candidate.get("passed").asBoolean() + "," +
                "\"finished\":" + candidate.get("finished").asBoolean() + "," +
                "\"rated\":" + candidate.get("rated").asBoolean() + "," +
                "\"points\":" + candidate.get("points").asDouble() +
                "}";
        return result;
    }

    protected static String getCandidateAsJsonString(String username, String answers, boolean passed, boolean finished, boolean rated, double points) {
        String result = "{" +
                "\"username\":\"" + username + "\"," +
                "\"answers\":" + answers + "," +
                "\"passed\":" + passed + "," +
                "\"finished\":" + finished + "," +
                "\"rated\":" + rated + "," +
                "\"points\":" + points +
                "}";
        return result;
    }

    protected static String getCandidateAnswersAsJsonString(JsonNode answers) {
        String json = "[";
        for (int i = 0; i < answers.size(); i++) {
            JsonNode answer = answers.get(i);
            json += getCandidateAnswerAsJsonString(answer);
            json += i != answers.size() - 1 ? "," : "";
        }
        json += "]";
        return json;
    }

    protected static String getCandidateAnswerAsJsonString(JsonNode answer) {
        String result = "{" +
                "\"question\": \"" + answer.get("question").asText() + "\"," +
                "\"type\": \"" + answer.get("type").asText() + "\"," +
                "\"content\": \"" + answer.get("content").asText() + "\"," +
                "\"correct\": \"" + answer.get("correct").asBoolean() + "\"," +
                "\"points\": \"" + answer.get("points").asDouble() + "\"," +
                "\"rated\": \"" + answer.get("rated").asBoolean() + "\"" +
                "}";
        return result;
    }

    protected static String getCandidateAnswerAsJsonString(
            String question, String type, String content, boolean correct, boolean rated, double points) {

        String result = "{" +
                "\"question\": \"" + question + "\"," +
                "\"type\": \"" + type + "\"," +
                "\"content\": \"" + content + "\"," +
                "\"correct\": \"" + correct + "\"," +
                "\"points\": \"" + points + "\"," +
                "\"rated\": \"" + rated + "\"" +
                "}";
        return result;
    }

    protected static String getQuestionsAsJsonString(JsonNode rootNode) {
        String questions = new String("[");

        JsonNode allQuestionsNode = rootNode.get("questions");
        int allQuestionsNodeSize = allQuestionsNode.size();

        for (int i = 0; i < allQuestionsNodeSize; i++) {
            JsonNode singleQuestionNode = allQuestionsNode.get(i);
            String type = singleQuestionNode.get("type").asText();
            String content = singleQuestionNode.get("content").asText();
            double points = singleQuestionNode.get("points").asDouble();

            questions += "{"+
                    "\"content\":\"" + content + "\"," +
                    "\"points\":\"" + points + "\"," +
                    "\"type\":\"" + type + "\"";
            if (!type.contentEquals("O")) {
                questions += ",";
            }

            if (type.contentEquals("W")) {
                JsonNode allAnswersNode = singleQuestionNode.get("answers");
                int allAnswersNodeSize = allAnswersNode.size();

                questions += "\"answers\": [";
                for (int j = 0; j < allAnswersNodeSize; j++) {
                    JsonNode singleAnswerNode = allAnswersNode.get(j);
                    String answer = singleAnswerNode.get("answer").asText();
                    Boolean correct = singleAnswerNode.get("correct").asBoolean();

                    questions += "{"+
                            "\"answer\":\"" + answer + "\"," +
                            "\"correct\":" + correct +
                            "}";
                    if (j != allAnswersNodeSize - 1) {
                        questions += ",";
                    } else {
                        questions += "]";
                    }
                }
            } else if (type.contentEquals("L")) {
                double correctAnswer = singleQuestionNode.get("correct").asDouble();
                questions += "\"correct\":" + correctAnswer;
            } else if (type.contentEquals("O")) {

            } else {
                // wyjątek
            }
            questions += "}";
            if (i != allQuestionsNodeSize - 1) {
                questions += ",";
            }
        }
        questions += "]";

        return questions;
    }

    protected static String removeAttributeFromTest(String item, String attribute) throws IOException {
        JsonNode test = new ObjectMapper().readValue(item, JsonNode.class);
        ((ObjectNode) test).remove(attribute);
        return test.toString();
    }

    protected static String removeCandidatesFromTest(String itemAsString) throws IOException {
        JsonNode test = new ObjectMapper().readValue(itemAsString, JsonNode.class);
        ((ObjectNode) test).remove("candidates");
        return test.toString();
    }

    protected static String removeCorrectAnswersFromTest(String itemAsString) throws IOException {
        JsonNode test = new ObjectMapper().readValue(itemAsString, JsonNode.class);
        JsonNode questions = test.get("questions");
        for (int i = 0; i < questions.size(); i++) {
            JsonNode question = questions.get(i);
            if (question.get("type").asText().contentEquals("W")) {
                JsonNode answers = question.get("answers");
                for (int j = 0; j < answers.size(); j++) {
                    ((ObjectNode) answers.get(j)).remove("correct");
                }
            } else if (question.get("type").asText().contentEquals("L")) {
                ((ObjectNode) question).remove("correct");
            }
        }
        return test.toString();
    }
}