package com.allardworks.workinator3.contracts;

public interface Workinator {
    Assignment getAssignment(ExecutorStatus executorId);

    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException;

    void unregisterConsumer(ConsumerRegistration registration);

    void createPartition(CreatePartitionCommand command) throws PartitionExistsException;
}
