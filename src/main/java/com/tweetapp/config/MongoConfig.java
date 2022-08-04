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
        ConnectionString connectionString = new ConnectionString(
                "mongodb://ganixx:Hpacer3799@cluster0-shard-00-00.xkyvl.mongodb.net:27017,cluster0-shard-00-01.xkyvl.mongodb.net:27017,cluster0-shard-00-02.xkyvl.mongodb.net:27017/?ssl=true&replicaSet=atlas-uomefy-shard-0&authSource=admin&retryWrites=true&w=majority");
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
