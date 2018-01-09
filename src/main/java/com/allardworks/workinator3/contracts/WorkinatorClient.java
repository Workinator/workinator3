package com.allardworks.workinator3.contracts;

/**
 * Created by jaya on 1/9/18.
 * k?
 */
public interface WorkinatorClient {
    Assignment getAssignment(ExecutorId executorId);

    void releaseAssignment(Assignment assignment);

    ConsumerRegistration registerConsumer(ConsumerId id) throws ConsumerExistsException;

    void unregisterConsumer(ConsumerRegistration registration);

    void createPartition(final CreatePartitionCommand command) throws PartitionExistsException;
}
