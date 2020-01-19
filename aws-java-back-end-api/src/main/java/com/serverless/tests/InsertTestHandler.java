package com.serverless.tests;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InsertTestHandler  implements RequestStreamHandler {

    private static final Logger LOG = LogManager.getLogger(InsertTestHandler.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);
        Table tests = DynamoDbController.getTable("Tests");
        Item test = createTestItem(rootNode);
        tests.putItem(test);
    }

    private Item createTestItem(JsonNode rootNode) {
        String recruiterId = rootNode.get("recruiterId").asText();
        String testId = "test" + getCurrentDateAndTime();
        String testName = rootNode.get("testName").asText();
        double minPoints = rootNode.get("minPoints").asDouble();
        double maxPoints = rootNode.get("maxPoints").asDouble();
        String questions = JsonFormatter.getQuestionsAsJsonString(rootNode);

        PrimaryKey primaryKey = new PrimaryKey("recruiterId", recruiterId, "testId", testId);
        Item test = new Item().withPrimaryKey(primaryKey).withString("testName", testName)
                .withDouble("minPoints", minPoints).withDouble("maxPoints", maxPoints)
                .withJSON("questions", questions).withList("candidates", new ArrayList<>());

        return test;
    }

    private String getCurrentDateAndTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddhhmmssMs");
        return simpleDateFormat.format(date);
    }
}
