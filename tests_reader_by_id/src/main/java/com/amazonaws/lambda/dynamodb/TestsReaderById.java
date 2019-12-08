package com.amazonaws.lambda.dynamodb;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestsReaderById implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);
		String username = rootNode.get("username").asText();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		String tableName = "Tests";
		Table tests = dynamoDB.getTable(tableName);

		ScanSpec scanSpec = new ScanSpec();
		scanSpec = scanSpec.withProjectionExpression(
				"recruiter_id, test_id, max_points, min_points, questions, test_name, candidates");
		ItemCollection<ScanOutcome> items = tests.scan(scanSpec);
		System.out.println("ccccc");
		Iterator<Item> iterator = items.iterator();

		List<String> itemsAsStrings = new ArrayList<String>();
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
					String itemAsString = item.toJSONPretty() + ",";
					itemsAsStrings.add(itemAsString);
					continue;
				}
			}
		}
		outputStream = new BufferedOutputStream(outputStream);
		outputStream.write("[".getBytes());
		if (itemsAsStrings.size() != 0) {
			for (int i = 0; i < itemsAsStrings.size(); i++) {
				if (i == itemsAsStrings.size() - 1) {
					String element = itemsAsStrings.get(i).substring(0, itemsAsStrings.get(i).length() - 1);
					itemsAsStrings.set(i, element);
				}
				outputStream.write(itemsAsStrings.get(i).getBytes());
			}
		}
		outputStream.write("]".getBytes());
		outputStream.flush();
	}

}
