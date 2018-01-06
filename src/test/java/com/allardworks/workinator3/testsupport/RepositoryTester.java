package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.PartitionDao;
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
        final List<PartitionDao> partitions =
                IntStream
                .range(0, howMany)
                .mapToObj(i -> {
                    val p = new PartitionDao();
                    p.setPartitionKey("Key-" + i);
                    return p;
                })
                .collect(toList());
        getAdminRepository().createPartitions(partitions);
    }
}
