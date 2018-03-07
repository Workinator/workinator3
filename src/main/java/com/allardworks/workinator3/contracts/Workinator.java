package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.commands.*;

import java.util.List;

public interface Workinator extends AutoCloseable {
    Assignment getAssignment(WorkerStatus executorId);

    void releaseAssignment(ReleaseAssignmentCommand assignment);

    ConsumerRegistration registerConsumer(RegisterConsumerCommand command) throws ConsumerExistsException;

    void unregisterConsumer(UnregisterConsumerCommand command);

    void createPartition(CreatePartitionCommand command) throws PartitionExistsException;

    void updateWorkerStatus(UpdateWorkersStatusCommand workerStatus);

    void updateConsumerStatus(UpdateConsumerStatusCommand consumerStatus);

    List<PartitionInfo> getPartitions();

    PartitionConfiguration getPartitionConfiguration(String partitionKey);
}
