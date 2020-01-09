package com.serverless;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class ReadTestsHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Table tests = DynamoDbController.getTable("Tests");
        Iterator<Item> iterator = DynamoDbController.getItemsFromTable(
                "recruiter_id, test_id, max_points, min_points, questions, test_name", tests);
        writeItemsToOutputStream(iterator, outputStream);
    }

    private void writeItemsToOutputStream(Iterator<Item> iterator, OutputStream outputStream) throws IOException {
        outputStream = new BufferedOutputStream(outputStream);
        outputStream.write("[".getBytes());
        while (iterator.hasNext()) {
            Item item = iterator.next();
            String itemAsString = (iterator.hasNext()) ? item.toJSONPretty() + "," : item.toJSONPretty();
            outputStream.write(itemAsString.getBytes());
        }
        outputStream.write("]".getBytes());
        outputStream.flush();
    }
}