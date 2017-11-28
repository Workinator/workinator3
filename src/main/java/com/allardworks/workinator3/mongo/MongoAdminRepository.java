package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.core.ConvertUtility;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.NonNull;
import lombok.val;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.tools.JavaCompiler;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoAdminRepository implements WorkinatorAdminRepository {
    private final MongoClient client;
    private final MongoCollection<Document> partitions;

    public MongoAdminRepository(@NonNull final MongoConfiguration config) {
        client = new MongoClient(config.getHost(), config.getPort());
        setupDatabase();
        partitions = client.getDatabase(config.getDatabaseName()).getCollection(config.getCollectionName(), Document.class);
    }

    private void setupDatabase() {
    }

    @Override
    public PartitionDto create(final PartitionDto partition) {
        val doc = new Document();
        doc.put("partitionKey", partition.getPartitionKey());
        doc.put("synchronizationKey", partition.getSynchronizationKey());

        doc.put("maxIdleSeconds", partition.getMaxIdleTimeSeconds());
        doc.put("maxWorkerCount", partition.getMaxWorkerCount());
        doc.put("hasMoreWork", partition.isHasMoreWork());
        doc.put("workCount", partition.getWorkCount());
        doc.put("synchronizationKey", partition.getSynchronizationKey());

        if (partition.getLastCheck() == null) {
            doc.put("lastCheck", null);
        } else {
            doc.put("lastCheck", ConvertUtility.toDate(partition.getLastCheck()));
        }

        if (partition.getLastWork() == null) {
            doc.put("lastWork", null);
        } else {
            doc.put("lastWork", ConvertUtility.toDate(partition.getLastWork()));
        }

        partitions.insertOne(doc);
        return getPartition(partition.getPartitionKey());
    }

    @Override
    public PartitionDto update(String partitionKey, PartitionDto partition) {
        return null;
    }

    @Override
    public PartitionDto delete(PartitionDto partition) {
        return null;
    }

    public PartitionDto getPartition(@NonNull final String partitionKey) {
        val doc = partitions.find(eq("partitionKey", partitionKey)).first();
        val partition = new PartitionDto();
        partition.setPartitionKey(doc.getString("partitionKey"));
        partition.setHasMoreWork(doc.getBoolean("hasMoreWork"));
        partition.setMaxIdleTimeSeconds(doc.getInteger("maxIdleSeconds"));
        partition.setWorkCount(doc.getLong("workCount"));
        partition.setMaxWorkerCount(doc.getInteger("maxWorkerCount"));
        partition.setSynchronizationKey(doc.get("synchronizationKey", UUID.class));
        {
            val lastWork = doc.get("lastWork");
            if (lastWork != null) {
                val lastWorkDate = ((Date) lastWork);
                partition.setLastWork(ConvertUtility.toLocalDateTime(lastWorkDate));

            }
        }

        {
            val lastCheck = doc.get("lastCheck");
            if (lastCheck != null) {
                val lastCheckDate = ((Date) lastCheck);
                partition.setLastCheck(ConvertUtility.toLocalDateTime(lastCheckDate));
            }
        }

        return partition;
    }
}
