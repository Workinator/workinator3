package com.allardworks.workinator3.httpapi;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.contracts.Workinator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private final Workinator workinator;

    @GetMapping("consumers")
    public Iterable<Consumer> getConsumers() {
        return consumerService.getConsumers();
    }

    @GetMapping("partitions")
    public Iterable<Partition> getPartitions() {
        return partitionService.getPartitions();
    }

    @GetMapping("partitions/{partitionKey}")
    public Partition getPartition(@PathVariable("partitionKey") final String partitionKey) {
        return partitionService.getPartition(partitionKey);
    }

    @PutMapping("partitions/{partitionKey}")
    public Partition createPartition(@PathVariable("partitionKey") String partitionKey, @RequestBody final CreatePartitionRequest request) throws PartitionExistsException {
        // TODO: currently results in 500 if partition exists.
        // should be idempotent
        val createCommand = CreatePartitionCommand
                .builder()
                .partitionKey(partitionKey)
                .maxWorkerCount(request.getMaxWorkerCount())
                .maxIdleTimeSeconds(request.getMaxIdleTimeSeconds())
                .build();
        workinator.createPartition(createCommand);
        return partitionService.getPartition(partitionKey);
    }
}
