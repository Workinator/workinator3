package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ConsumerConfiguration;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.WorkerFactory;
import com.allardworks.workinator3.contracts.WorkinatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates an instance of the WorkinatorConsumer.
 * This is a convenience in cases where a single program
 * wants to createPartitions multiple consumers. Usually, that's not how WorkinatorConsumer should
 * be used. But, for tests and demos, you may need multiple.
 * In a usual program, you would just createPartitions the beans and inject WorkinatorConsumer, letting spring do the work.
 */
@Component
@RequiredArgsConstructor
public class WorkinatorConsumerFactory {
    @Autowired
    private final ConsumerConfiguration consumerConfiguration;

    @Autowired
    private final WorkinatorRepository workinatorRepository;

    @Autowired
    private final ExecutorFactory executorFactory;

    @Autowired
    private final WorkerFactory workerFactory;

    public WorkinatorConsumer create(final ConsumerId id) {
        return new WorkinatorConsumer(consumerConfiguration, workinatorRepository, executorFactory, workerFactory, id);
    }
}
