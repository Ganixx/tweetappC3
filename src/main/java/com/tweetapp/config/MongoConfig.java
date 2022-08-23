package com.tweetapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;

@Configuration
@EnableMongoRepositories("com.tweetapp")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return createMongoClient(settings);
    }

    @Override
    protected String getDatabaseName() {
        return "tweetapp";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

}
