package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.commands.UpdateWorkerStatusCommand;

import java.util.List;

public interface Workinator extends AutoCloseable {
    Assignment getAssignment(WorkerStatus executorId);

    void releaseAssignment(ReleaseAssignmentCommand assignment);

    ConsumerRegistration registerConsumer(RegisterConsumerCommand command) throws ConsumerExistsException;

    void unregisterConsumer(ConsumerRegistration registration);

    void createPartition(CreatePartitionCommand command) throws PartitionExistsException;

    void updateStatus(UpdateWorkerStatusCommand workerStatus);

    List<PartitionInfo> getPartitions();
}
