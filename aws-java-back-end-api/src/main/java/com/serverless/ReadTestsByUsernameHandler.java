package com.serverless;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReadTestsByUsernameHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Table tests = DynamoDbController.getTable("Tests");
        InputStreamSelector iss = new InputStreamSelector(inputStream);
        String username = iss.getAttributeAsText("username");
        Iterator<Item> iterator = DynamoDbController.getItemsFromTable(
                "recruiter_id, test_id, max_points, min_points, questions, test_name, candidates", tests);

        List<String> itemsAsStrings = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            JsonNode test = objectMapper.readValue(item.toJSON(), JsonNode.class);
            JsonNode candidates = test.get("candidates");
            if (candidates == null) {
                continue;
            }
            for (int i = 0; i < candidates.size(); i++) {
                JsonNode candidate = candidates.get(i);
                String candidateUsername = candidate.get("username").asText();
                if (candidateUsername.equals(username)) {
                    String itemAsString = item.toJSONPretty();
                    itemAsString = JsonFormatter.removeCandidatesFromTest(itemAsString);

                    itemAsString = JsonFormatter.removeCorrectAnswersFromTest(itemAsString);
                    itemsAsStrings.add(itemAsString);
                    break;
                }
            }
        }
        writeItemsToOutputStream(itemsAsStrings, outputStream);
    }

    private void writeItemsToOutputStream(List<String> itemsAsStrings, OutputStream outputStream) throws IOException {
        outputStream = new BufferedOutputStream(outputStream);
        outputStream.write("[".getBytes());
        for (int i = 0; i < itemsAsStrings.size(); i++) {
            String itemAsString = i == itemsAsStrings.size() - 1 ? itemsAsStrings.get(i) : itemsAsStrings.get(i) + ",";
            outputStream.write(itemAsString.getBytes());
        }
        outputStream.write("]".getBytes());
        outputStream.flush();
    }
}