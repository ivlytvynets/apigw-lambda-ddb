package com.example.utils;

import com.example.model.User;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DDBUtils {
    private final DynamoDbEnhancedClient enhancedClient;
    private final String tableName;

    public DDBUtils(String tableName, String region) {
        this.tableName = tableName;
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.of(region))
            .build();
        enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }

    public String addRankRecord(User user) {
        DynamoDbTable<User> userTable = enhancedClient
            .table(tableName, TableSchema.fromBean(User.class));
        return userTable.updateItem(user).getNickName();
    }

    public User getRankRecord(String userId) {
        DynamoDbTable<User> userTable = enhancedClient
            .table(tableName, TableSchema.fromBean(User.class));
        Key key = Key.builder()
            .partitionValue(userId)
            .build();
        return userTable.getItem(key);
    }
}
