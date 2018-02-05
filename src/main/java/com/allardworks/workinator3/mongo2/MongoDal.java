package com.allardworks.workinator3.mongo2;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class MongoDal implements AutoCloseable {
    @Getter
    private final MongoConfiguration config;

    @Getter
    private final MongoClient client;

    @Getter
    private final MongoCollection<Document> partitionsCollection;

    @Getter
    private final MongoCollection<Document> consumersCollection;

    @Getter
    private final MongoCollection<Document> workersCollection;

    @Getter
    private final MongoDatabase database;

    public MongoDal(@NonNull MongoConfiguration config) {
        this.config = config;
        client = new MongoClient(config.getHost(), config.getPort());
        database = client.getDatabase(config.getDatabaseName());
        partitionsCollection = database.getCollection(config.getPartitionsCollectionName(), Document.class);
        workersCollection = database.getCollection(config.getWorkersCollectionName(), Document.class);
        consumersCollection = database.getCollection("Consumers", Document.class);
        setupDatabase();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }

    private void setupDatabase() {
        // ------------------------------------------------
        // consumers - primary key
        // ------------------------------------------------
        val pkConsumer = new BasicDBObject().append("consumerId", 1);
        val pkConsumerOptions = new IndexOptions().name("consumerId").unique(true).background(false);
        consumersCollection.createIndex(pkConsumer, pkConsumerOptions);

        // ------------------------------------------------
        // partitions - primary key
        // ------------------------------------------------
        val pkPartition = new BasicDBObject().append("partitionKey", 1);
        val pkPartitionOptions = new IndexOptions().name("partitionKey").unique(true).background(false);
        partitionsCollection.createIndex(pkPartition, pkPartitionOptions);

        // ------------------------------------------------
        // workers - primary key
        // ------------------------------------------------
        val pkWorker = new BasicDBObject().append("partitionKey", 1).append("workerNumber", 1);
        val pkWorkerOptions = new IndexOptions().name("primary key").unique(true).background(false);
        workersCollection.createIndex(pkWorker, pkWorkerOptions);

        // ------------------------------------------------
        // workers - rule 1
        // ------------------------------------------------
        val rule1 = new BasicDBObject().append("workerNumber", 1).append("currentAssignee", 1).append("lastCheckEnd", 1);
        val rule1options = new IndexOptions().name("rule1").unique(false).background(false);
        workersCollection.createIndex(rule1, rule1options);

        // ------------------------------------------------
        // workers - release
        // ------------------------------------------------
        val release = new BasicDBObject().append("partitionKey", 1).append("workerNumber", 1).append("currentAssignee", 1);
        val options = new IndexOptions().name("release").unique(false).background(false);
        workersCollection.createIndex(release, options);
    }
}
