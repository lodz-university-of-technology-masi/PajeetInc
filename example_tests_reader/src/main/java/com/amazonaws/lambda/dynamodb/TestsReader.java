package com.amazonaws.lambda.dynamodb;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TestsReader implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		String tableName = "Tests";
		Table tests = dynamoDB.getTable(tableName);

		ScanSpec scanSpec = new ScanSpec();
		scanSpec = scanSpec.withProjectionExpression("recruiter_id, test_id, max_points, min_points, questions");
		ItemCollection<ScanOutcome> items = tests.scan(scanSpec);
		
		outputStream = new BufferedOutputStream(outputStream);
		Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            String itemAsString = (iterator.hasNext()) ? item.toJSONPretty()+"," : item.toJSONPretty();
            outputStream.write(itemAsString.getBytes());
//            itemAsString = itemAsString.replaceAll("\"", Character.toString('"'));
//            itemAsString = itemAsString.replaceAll("\n", "");
        }
        outputStream.flush();
    }
}