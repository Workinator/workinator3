package com.allardworks.workinator3.httpapi;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ConsumerRepository repository;

    public Iterable<Consumer> getConsumers() {
        return repository.findAll();
    }
}
