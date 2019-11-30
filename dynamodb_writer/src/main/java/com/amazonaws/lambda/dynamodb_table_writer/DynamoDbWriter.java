package com.amazonaws.lambda.dynamodb_table_writer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class DynamoDbWriter implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readValue(inputStream, JsonNode.class);

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		String tableName = "Tests";
		Table tests = dynamoDB.getTable(tableName);
		
		Item test = createTestItem(rootNode);
		tests.putItem(test);
	}
	
	private Item createTestItem(JsonNode rootNode) {
		String recruiterId = rootNode.get("recruiterId").asText();
		String testId = "test" + getCurrentDateAndTime();
		String testName = rootNode.get("testName").asText();
		int minPoints = rootNode.get("minPoints").asInt();
		int maxPoints = rootNode.get("maxPoints").asInt();
		String questions = getQuestionsAsJson(rootNode);
		
		PrimaryKey primaryKey = new PrimaryKey("recruiter_id", recruiterId, "test_id", testId);
		Item test = new Item().withPrimaryKey(primaryKey).withString("test_name", testName)
				.withInt("min_points", minPoints).withInt("max_points", maxPoints).withJSON("questions", questions);
		
		return test;
	}
	
	private String getQuestionsAsJson(JsonNode rootNode) {
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
	
	private String getCurrentDateAndTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddhhmmssMs");
        return simpleDateFormat.format(date);
	}
}