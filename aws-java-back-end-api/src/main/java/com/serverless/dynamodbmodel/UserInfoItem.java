package com.serverless.dynamodbmodel;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="UserInfo")
public class UserInfoItem {
    private String id;
    private String username;

    @DynamoDBHashKey(attributeName="id")
    public String getId() { return id; }
    public void setId(String id) {this.id = id; }

    @DynamoDBAttribute(attributeName="username")
    public String getUsername() {return username; }
    public void setUsername(String username) { this.username = username; }

}