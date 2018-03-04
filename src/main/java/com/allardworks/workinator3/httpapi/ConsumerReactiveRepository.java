package com.allardworks.workinator3.httpapi;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
public interface ConsumerReactiveRepository extends ReactiveCrudRepository<Consumer, String> {
}
