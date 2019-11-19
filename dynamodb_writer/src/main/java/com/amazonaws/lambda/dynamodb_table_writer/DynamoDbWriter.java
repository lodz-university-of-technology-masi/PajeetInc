package com.amazonaws.lambda.dynamodb_table_writer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class DynamoDbWriter implements RequestHandler<Request, String> {

    @Override
    public String handleRequest(Request input, Context context) {

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		DynamoDB dynamoDB = new DynamoDB(client);
		String tableName = "Questions";
		
		Table table = dynamoDB.getTable(tableName);
		
		try {

			String question = input.question;
			Item item = new Item().withPrimaryKey("id", 100).withString("question", question);
			table.putItem(item);
			
//			Item item = new Item().withPrimaryKey("Id", 120).withString("Title", "Book 120 Title")
//					.withString("ISBN", "120-1111111111")
//					.withStringSet("Authors", new HashSet<String>(Arrays.asList("Author12", "Author22")))
//					.withNumber("Price", 20).withString("Dimensions", "8.5x11.0x.75").withNumber("PageCount", 500)
//					.withBoolean("InPublication", false).withString("ProductCategory", "Book");
//			table.putItem(item);
//
//			item = new Item().withPrimaryKey("Id", 121).withString("Title", "Book 121 Title")
//					.withString("ISBN", "121-1111111111")
//					.withStringSet("Authors", new HashSet<String>(Arrays.asList("Author21", "Author 22")))
//					.withNumber("Price", 20).withString("Dimensions", "8.5x11.0x.75").withNumber("PageCount", 500)
//					.withBoolean("InPublication", true).withString("ProductCategory", "Book");
//			table.putItem(item);

		} catch (Exception e) {
			System.err.println("Create items failed.");
			System.err.println(e.getMessage());
		}

		return "Hello from Lambda!";
    }

}