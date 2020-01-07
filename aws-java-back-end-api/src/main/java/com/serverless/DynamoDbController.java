package com.serverless;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.Iterator;

public class DynamoDbController {

    protected static Table getTable(String tableName) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        DynamoDB dynamoDB = new DynamoDB(client);
        return dynamoDB.getTable(tableName);
    }

    protected static Iterator<Item> getItemsFromTable(String projectionExpression, Table table) {
        ScanSpec scanSpec = new ScanSpec();
        scanSpec = scanSpec.withProjectionExpression(projectionExpression);
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        return items.iterator();
    }

    protected static Item getItemByPrimaryKey(PrimaryKey primaryKey, Table table) {
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey(primaryKey);
        return table.getItem(spec);
    }

    protected  static void updateCandidates(PrimaryKey primaryKey, String candidates, Table tests) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(primaryKey)
                .withUpdateExpression("set candidates=:c")
                .withValueMap(new ValueMap().withJSON(":c", candidates));
        tests.updateItem(updateItemSpec);
    }
}