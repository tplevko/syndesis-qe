package io.syndesis.qe.bdd.validation;

import io.syndesis.qe.utils.DynamoDbUtils;
import io.syndesis.qe.utils.TestUtils;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamoDbValidationSteps {

    @Autowired
    private DynamoDbUtils dynamoDb;

    String

    @Given("^purge dynamoDb DB$")
    public void purge() {
        dynamoDb.purge(
            "music"
        );
        // Purging a queue "may take up to 60 seconds"
        TestUtils.sleepIgnoreInterrupt(60000L);
    }

    @Given("create new dynamoDb table")
    public void createTable() {
        dynamoDb.purge(
            "music"
        );
        // Purging a queue "may take up to 60 seconds"
        TestUtils.sleepIgnoreInterrupt(60000L);
    }

}
