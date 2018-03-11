package com.allardworks.workinator3.httpapi;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
public interface PartitionRepository extends MongoRepository<Partition, String> {
    Partition findByPartitionKey(String partitionKey);
}
