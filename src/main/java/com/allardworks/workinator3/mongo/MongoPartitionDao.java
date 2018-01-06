package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.PartitionDao;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

public class MongoPartitionDao extends PartitionDao {
    @BsonId
    public UUID id;
}
