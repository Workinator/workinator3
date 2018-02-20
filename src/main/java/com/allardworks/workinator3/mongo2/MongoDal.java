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
    private final MongoDatabase database;

    public MongoDal(@NonNull MongoConfiguration config) {
        this.config = config;
        client = new MongoClient(config.getHost(), config.getPort());
        database = client.getDatabase(config.getDatabaseName());
        partitionsCollection = database.getCollection(config.getPartitionsCollectionName(), Document.class);

        // TODO: name consumer collection by particion type
        consumersCollection = database.getCollection(config.getConsumersCollectionName(), Document.class);
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
        val pkConsumer = new BasicDBObject().append("name", 1);
        val pkConsumerOptions = new IndexOptions().name("name").unique(true).background(false);
        consumersCollection.createIndex(pkConsumer, pkConsumerOptions);

        // ------------------------------------------------
        // partitions - primary key
        // ------------------------------------------------
        val pkPartition = new BasicDBObject().append("partitionKey", 1);
        val pkPartitionOptions = new IndexOptions().name("partitionKey").unique(true).background(false);
        partitionsCollection.createIndex(pkPartition, pkPartitionOptions);
    }
}
