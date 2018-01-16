package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.*;
import lombok.NonNull;
import lombok.val;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface RepositoryTester extends AutoCloseable {
    WorkinatorAdminRepository getAdminRepository();
    WorkinatorRepository getRepository();

    default void createPartitions(@NonNull final int howMany) throws PartitionExistsException {
        final List<PartitionDao> partitions =
                IntStream
                .range(0, howMany)
                .mapToObj(i -> {
                    val p = new PartitionDao();
                    p.setPartitionKey("Key-" + i);
                    p.getMaxWorkerCount().setValue(1);
                    return p;
                })
                .collect(toList());
        getAdminRepository().createPartitions(partitions);
    }

    default boolean consumerExists(@NonNull final String consumerId) {
        try {
            getRepository().getConsumer(consumerId);
            return true;
        } catch (final ConsumerDoesntExistsException e) {
            return false;
        }
    }
}
