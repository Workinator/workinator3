package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.PartitionDto;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.allardworks.workinator3.contracts.WorkinatorAdminRepository;
import com.allardworks.workinator3.contracts.WorkinatorRepository;
import lombok.val;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface RepositoryTester extends AutoCloseable {
    WorkinatorAdminRepository getAdminRepository();
    WorkinatorRepository getRepository();

    default void createPartitions(final int howMany) throws PartitionExistsException {
        final List<PartitionDto> partitions =
                IntStream
                .range(0, howMany)
                .mapToObj(i -> {
                    val p = new PartitionDto();
                    p.setPartitionKey("Key-" + i);
                    return p;
                })
                .collect(toList());
        getAdminRepository().createPartitions(partitions);
    }
}
