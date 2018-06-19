package com.allardworks.workinator3.core;

import com.allardworks.workinator3.core.commands.*;

import java.util.List;

public interface Workinator extends AutoCloseable {
    Assignment getAssignment(WorkerStatus executorId);

    void releaseAssignment(ReleaseAssignmentCommand assignment);

    ConsumerRegistration registerConsumer(RegisterConsumerCommand command) throws ConsumerExistsException;

    void unregisterConsumer(UnregisterConsumerCommand command);

    void createPartition(CreatePartitionCommand command) throws PartitionExistsException;

    void setPartitionStatus(SetPartitionStatusCommand command);

    void updateConsumerStatus(UpdateConsumerStatusCommand consumerStatus);

    List<PartitionInfo> getPartitions();

    List<ConsumerInfo> getConsumers();

    PartitionConfiguration getPartitionConfiguration(String partitionKey);
}
