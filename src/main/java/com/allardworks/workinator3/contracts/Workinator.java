package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;

public interface Workinator extends AutoCloseable {
    Assignment getAssignment(ExecutorStatus executorId);

    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(RegisterConsumerCommand command) throws ConsumerExistsException;

    void unregisterConsumer(ConsumerRegistration registration);

    void createPartition(CreatePartitionCommand command) throws PartitionExistsException;
}
