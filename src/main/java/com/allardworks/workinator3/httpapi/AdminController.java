package com.allardworks.workinator3.httpapi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Created by jaya on 2/28/18.
 * k?
 */
@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class AdminController {
    private final ConsumerService consumerService;
    private final PartitionService partitionService;

    @GetMapping("consumers")
    public Flux<Consumer> getConsumers() {
        return consumerService.getConsumers();
    }

    @GetMapping("partitions")
    public Flux<Partition> getPartitions() {
        return partitionService.getPartitions();
    }
}
