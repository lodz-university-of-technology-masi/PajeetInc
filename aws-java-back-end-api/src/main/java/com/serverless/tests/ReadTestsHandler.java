package com.serverless.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class ReadTestsHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Table tests = DynamoDbController.getTable("Tests");
        Iterator<Item> iterator = DynamoDbController.getItemsFromTable(
                "recruiterId, testId, maxPoints, minPoints, questions, testName", tests);
        DynamoDbController.writeItemsToOutputStream(iterator, outputStream);
    }
}