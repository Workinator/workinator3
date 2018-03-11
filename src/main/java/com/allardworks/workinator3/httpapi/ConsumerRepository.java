package com.allardworks.workinator3.httpapi;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
public interface ConsumerRepository extends MongoRepository<Consumer, String> {
}
