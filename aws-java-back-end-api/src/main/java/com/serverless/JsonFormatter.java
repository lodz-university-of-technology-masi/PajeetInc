package com.serverless;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;

public class JsonFormatter {

    protected static String getCandidatesAsJsonString(
            String username, String answers, boolean passed, boolean finished, int points, Item test) throws IOException {
        String result = "[";
        Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
        while (candidates.hasNext()) {
            JsonNode candidate = candidates.next();
            if (!candidate.get("username").asText().contains(username)) {
                result += getCandidateAsJsonString(candidate);
            } else {
                result += getCandidateAsJsonString(username, answers, passed, finished, points);
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
                "\"points\":" + candidate.get("points").asInt() +
                "}";
        return result;
    }

    protected static String getCandidateAsJsonString(String username, String answers, boolean passed, boolean finished, int points) {
        String result = "{" +
                "\"username\":\"" + username + "\"," +
                "\"answers\":" + answers + "," +
                "\"passed\":" + passed + "," +
                "\"finished\":" + finished + "," +
                "\"points\":" + points +
                "}";
        ;
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
                "\"correct\": \"" + answer.get("correct").asText() + "\"," +
                "\"rated\": \"" + answer.get("rated").asText() + "\"" +
                "}";
        return result;
    }

    protected static String getCandidateAnswerAsJsonString(String question, String type, String content, boolean correct, boolean rated) {
        String result = "{" +
                "\"question\": \"" + question + "\"," +
                "\"type\": \"" + type + "\"," +
                "\"content\": \"" + content + "\"," +
                "\"correct\": \"" + correct + "\"," +
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

            questions += "{\"question_content\":\"" + content + "\"," + "\"question_type\":\"" + type + "\"";
            if (!type.contains("O")) {
                questions += ",";
            }

            if (type.contains("W")) {
                JsonNode allAnswersNode = singleQuestionNode.get("answers");
                int allAnswersNodeSize = allAnswersNode.size();

                questions += "\"answers\": [";
                for (int j = 0; j < allAnswersNodeSize; j++) {
                    JsonNode singleAnswerNode = allAnswersNode.get(j);
                    String answer = singleAnswerNode.get("answer").asText();
                    Boolean correct = singleAnswerNode.get("correct").asBoolean();

                    questions += "{\"answer\":\"" + answer + "\"," + "\"correct\":" + correct + "}";
                    if (j != allAnswersNodeSize - 1) {
                        questions += ",";
                    } else {
                        questions += "]";
                    }
                }
            } else if (type.contains("L")) {
                int correctAnswer = singleQuestionNode.get("correctAnswer").asInt();
                questions += "\"correct_answer\":" + correctAnswer;
            } else if (type.contains("O")) {

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
}
