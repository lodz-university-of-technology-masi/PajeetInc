package com.serverless;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    protected static Iterator<Item> getAllTestsByRecruiterId(String recruiterId, Table tests) {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("recruiter_id = :id")
                .withValueMap(new ValueMap()
                        .withString(":id", recruiterId));
        Iterator<Item> items = tests.query(spec).iterator();
//        List<String> testsIds = new ArrayList<>();
//        while(items.hasNext()) {
//            testsIds.add(items.next().ge);
//        }
        return items;
    }
}