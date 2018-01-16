package com.allardworks.workinator3.mongo.testsupport;

import com.allardworks.workinator3.mongo.*;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

public class MongoRepositoryTester implements RepositoryTester {
    private final MongoDal dal;
    @Getter
    private final MongoAdminRepository adminRepository;

    @Getter
    private final MongoRepository repository;
    private final MongoConfiguration config;
    private final String databaseName;

    public String getPartitionType() {
        return "TestPartitionType";
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public MongoRepositoryTester() {
        databaseName = "test";
        config = new MongoConfiguration(getPartitionType(), "localhost", 27017, databaseName);
        dal = new MongoDal(config);
        adminRepository = new MongoAdminRepository(dal);
        repository = new MongoRepository(dal, new WhatsNextRebalanceStrategy(dal));

    }

    public MongoDatabase getDatabase() {
        return dal.getClient().getDatabase(config.getDatabaseName());
    }

    public MongoCollection<Document> getPartitionsCollection() {
        return getDatabase().getCollection(config.getPartitionsCollectionName(), Document.class);
    }

    public MongoCollection<Document> getWorkersCollection() {
        return getDatabase().getCollection(config.getWorkersCollectionName(), Document.class);
    }

    @Override
    public void close() throws Exception {
        dal.getClient().dropDatabase(config.getDatabaseName());
        dal.close();
    }
}
