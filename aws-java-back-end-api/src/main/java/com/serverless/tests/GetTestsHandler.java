package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(GetTestsHandler.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        init(outputStream);

        JsonNode rootNode = new ObjectMapper().readValue(inputStream, JsonNode.class);
        String user = rootNode.get("user").asText();
        String role = rootNode.get("role").asText();
        String status = rootNode.get("status").asText();

        if (role.contentEquals("candidate")) {
            if (status.contentEquals("assigned")) {
                getAssignedCandidateTests(user);
            } else if (status.contentEquals("finished")) {
                getFinishedCandidateTests(user);
            }
        } else if (role.contentEquals("recruiter")) {
            if (status.contentEquals("assigned")) {
                getRecruiterTestsByFinished(false, user);
            } else if (status.contentEquals("finished")) {
                getRecruiterTestsByFinished(true, user);
            } else if (status.contentEquals("rated")) {
                getRatedRecruiterTests(user);
            }
        }
        writeItemsToOutputStream();
    }

    private void init(OutputStream outputStream) {
        table = DynamoDbController.getTable("Tests");
        tests = DynamoDbController.getItemsFromTable(
                "recruiterId, testId, maxPoints, minPoints, questions, testName, candidates", table);
        objectMapper = new ObjectMapper();
        jsons = new ArrayList<>();
        this.outputStream = outputStream;
        candidateInTests = new ArrayList<>();
    }

    private void getAssignedCandidateTests(String user) throws IOException {
        List<JsonNode> tests = new ArrayList<>(
                findCandidateTests(user)
        );
        for (int i = 0; i < tests.size(); i++) {
            JsonNode test = tests.get(i);
            String json;
            JsonNode candidateInTest = candidateInTests.get(i);
            if (candidateInTest.get("finished").asBoolean() == false &&
                    candidateInTest.get("rated").asBoolean() == false) {
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
            if (candidateInTest.get("rated").asBoolean() == false &&
                    candidateInTest.get("finished").asBoolean() == true) {
                json = JsonFormatter.getCandidateAsJsonString(
                        user, "[]", false, true, false, 0);
                json = json.substring(0, json.length() - 1);
                json += ",";
                json += "\"" + "testName" + "\":\"" + test.get("testName").asText() + "\"";
                json += "}";
                jsons.add(json);
            } else if (candidateInTest.get("rated").asBoolean() == true &&
                    candidateInTest.get("finished").asBoolean() == true) {
                json = JsonFormatter.getCandidateAsJsonString(candidateInTest);
                json = json.substring(0, json.length() - 1);
                json += ",";
                json += "\"" + "testName" + "\":\"" + test.get("testName").asText() + "\"";
                json += "}";
                jsons.add(json);
            }
        }
    }

    private void getRecruiterTestsByFinished(boolean finished, String user) throws IOException {
        Iterator<Item> tests = DynamoDbController.getAllTestsByRecruiterId(user, table);
        while (tests.hasNext()) {
            Item test = tests.next();
            Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
            while (candidates.hasNext()) {
                JsonNode candidate = candidates.next();
                if (finished == candidate.get("finished").asBoolean()) {
                    String json = JsonFormatter.getCandidateAsJsonString(candidate);
                    json = json.substring(0, json.length() - 1);
                    json += ",";
                    json += "\"" + "testName" + "\":\"" + test.get("testName") + "\",";
                    json += "\"" + "testId" + "\":\"" + test.get("testId") + "\"";
                    json += "}";
                    jsons.add(json);
                }
            }
        }
    }

    private void getRatedRecruiterTests(String user) throws IOException {
        Iterator<Item> tests = DynamoDbController.getAllTestsByRecruiterId(user, table);
        while (tests.hasNext()) {
            Item test = tests.next();
            Iterator<JsonNode> candidates = new ObjectMapper().readValue(test.getJSONPretty("candidates"), JsonNode.class).iterator();
            while (candidates.hasNext()) {
                JsonNode candidate = candidates.next();
                if (candidate.get("rated").asBoolean()) {
                    String json = JsonFormatter.getCandidateAsJsonString(candidate);
                    json = json.substring(0, json.length() - 1);
                    json += ",";
                    json += "\"" + "testName" + "\":\"" + test.get("testName") + "\",";
                    json += "\"" + "testId" + "\":\"" + test.get("testId") + "\"";
                    json += "}";
                    jsons.add(json);
                }
            }
        }
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
                if (username.contentEquals(user)) {
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
