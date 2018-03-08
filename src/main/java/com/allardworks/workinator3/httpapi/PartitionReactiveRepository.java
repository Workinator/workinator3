package com.allardworks.workinator3.httpapi;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
public interface PartitionReactiveRepository extends ReactiveCrudRepository<Partition, String> {
    Mono<Partition> findByPartitionKey(String partitionKey);
}
