package com.allardworks.workinator3.httpapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Service
@RequiredArgsConstructor
public class PartitionService {
    private final PartitionRepository repository;

    public Iterable<Partition> getPartitions() {
        return repository.findAll();
    }

    public Partition getPartition(final String partitionKey) {
        return repository.findByPartitionKey(partitionKey);
    }
}
