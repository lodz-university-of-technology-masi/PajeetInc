package com.serverless;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetTestsHandler implements RequestStreamHandler {

    private Table table;
    private Iterator<Item> tests;
    private ObjectMapper objectMapper;
    private List<String> jsons;
    private List<JsonNode> candidateInTests;
    private OutputStream outputStream;

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        table = DynamoDbController.getTable("Tests");
        tests = DynamoDbController.getItemsFromTable(
                "recruiter_id, test_id, max_points, min_points, questions, test_name, candidates", table);
        objectMapper = new ObjectMapper();
        jsons = new ArrayList<>();
        this.outputStream = outputStream;
        candidateInTests = new ArrayList<>();

        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);
        String user = rootNode.get("user").asText();
        String role = rootNode.get("role").asText();
        String status = rootNode.get("status").asText();

        if (role.contains("candidate")) {
            if (status.contains("assigned")) {
                getAssignedCandidateTests(user);
            } else if (status.contains("finished")) {
                getFinishedCandidateTests(user);
            }
        } else if (role.contains("recruiter")) {
            if (status.contains("assigned")) {
                getAssignedRecruiterTests(user);
            } else if (status.contains("finished")) {
                getFinishedRecruiterTests(user);
            } else if (status.contains("rated")) {
                getRatedRecruiterTests(user);
            }
        }

        writeItemsToOutputStream();
    }

    private void getAssignedCandidateTests(String user) throws IOException {
        List<JsonNode> tests = new ArrayList<>(
                findCandidateTests(user)
        );
        for (int i = 0; i < tests.size(); i++) {
            JsonNode test = tests.get(i);
            String json;
            JsonNode candidateInTest = candidateInTests.get(i);
            if (!candidateInTest.get("finished").asBoolean()) {
                json = test.toString();
                json = JsonFormatter.removeCandidatesFromTest(json);
                json = JsonFormatter.removeCorrectAnswersFromTest(json);
                jsons.add(json);
            }
        }
    }

    private void getFinishedCandidateTests(String user) throws IOException {
        List<JsonNode> tests = new ArrayList<>(
                findCandidateTests(user)
        );
        for (int i = 0; i < tests.size(); i++) {
            JsonNode test = tests.get(i);
            String json = "";
            JsonNode candidateInTest = candidateInTests.get(i);
            if (!candidateInTest.get("rated").asBoolean()) {
                json = JsonFormatter.getCandidateAsJsonString(
                        user, "[]", false, true, false, 0);
                jsons.add(json);
            } else {
                json = JsonFormatter.getCandidateAsJsonString(candidateInTest);
                json = json.substring(0, json.length() - 1);
                json += ",";
                json += "\""+"testName"+"\":\"" + test.get("test_name").asText() + "\"";
                json += "}";
                jsons.add(json);
            }
        }
    }

    private void getAssignedRecruiterTests(String user) throws IOException {

    }

    private void getFinishedRecruiterTests(String user) throws IOException {

    }

    private void getRatedRecruiterTests(String user) throws IOException {

    }

    private List<JsonNode> findCandidateTests(String user) throws IOException {
        List<JsonNode> result = new ArrayList<>();
        while (tests.hasNext()) {
            Item item = tests.next();
            JsonNode test = objectMapper.readValue(item.toJSON(), JsonNode.class);
            JsonNode candidates = test.get("candidates");
            if (candidates == null) {
                continue;
            }
            for (JsonNode candidate : candidates) {
                String username = candidate.get("username").asText();
                if (username.contains(user)) {
                    result.add(test);
                    candidateInTests.add(candidate);
                    break;
                }
            }
        }
        return result;
    }

    private void writeItemsToOutputStream() throws IOException {
        outputStream = new BufferedOutputStream(outputStream);
        outputStream.write("[".getBytes());
        for (int i = 0; i < jsons.size(); i++) {
            String itemAsString = i == jsons.size() - 1 ? jsons.get(i) : jsons.get(i) + ",";
            outputStream.write(itemAsString.getBytes());
        }
        outputStream.write("]".getBytes());
        outputStream.flush();
    }
}
