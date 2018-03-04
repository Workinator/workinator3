package com.allardworks.workinator3.httpapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Created by jaya on 3/3/18.
 * k?
 */
@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ConsumerReactiveRepository repository;

    public Flux<Consumer> getConsumers() {
        return repository.findAll();
    }
}
