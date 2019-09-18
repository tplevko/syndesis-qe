package io.syndesis.qe.utils;

import io.syndesis.qe.accounts.Account;
import io.syndesis.qe.accounts.AccountsDirectory;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Slf4j
@Component
@Lazy
public class DynamoDbUtils {

    private DynamoDbClient client;

    @PostConstruct
    public void initClient() {
        log.info("Initializing DynamoDb client");

        final Account dynamoDb = AccountsDirectory.getInstance().getAccount(Account.Name.AWS)
            .orElseThrow(() -> new IllegalArgumentException("Unable to find AWS account"));
        final String region = dynamoDb.getProperty("region").toLowerCase().replaceAll("_", "-");
        final String accountId = dynamoDb.getProperty("accountId");

        client = DynamoDbClient.builder().region(Region.of(region))
            .credentialsProvider(() -> AwsBasicCredentials.create(dynamoDb.getProperty("accessKey"), dynamoDb.getProperty("secretKey"))).build();
    }

    public void createTable(String tableName) {
        client.createTable(CreateTableRequest.builder().tableName(tableName).build());
    }

    public void deleteTable(String tableName) {
        client.deleteTable(DeleteTableRequest.builder().tableName(tableName).build());
    }


    public void createItem(String tableName, Map<String, String> item) {

        Map<String, AttributeValue> input = new HashMap<>();
        input = item.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                e -> new AttributeValue(AttributeValue.builder().s(Map.Entry::getValue).build())));

        client.putItem(PutItemRequest.builder().item(input).tableName(tableName).build());
    }

    public void getItemByKey(String tableName, String itemKey) {
//        client.batchGetItem()
    }

    public void queryItem(String tableName) {
        client.batchGetItem(BatchGetItemRequest.builder().build());
    }

    public int getQueueSize(String tableName) {
    return -1;
    }

    public void purge(String tableName) {
    }
}
