package com.serverless;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSelector {
    private InputStream inputStream;

    public InputStreamSelector(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    protected String getAttributeAsText(String attribute) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readValue(inputStream, JsonNode.class);
        return node.get(attribute).asText();
    }

    protected static String getQuestionsAsJson(JsonNode rootNode) {
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