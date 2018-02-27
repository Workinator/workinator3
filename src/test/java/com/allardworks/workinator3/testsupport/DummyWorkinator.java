package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.commands.*;
import com.allardworks.workinator3.contracts.*;

import java.util.List;

public class DummyWorkinator implements Workinator {
    private Assignment next;

    public void setNextAssignment(final Assignment assignment) {
        next = assignment;
    }

    @Override
    public Assignment getAssignment(WorkerStatus executorId) {
        return next;
    }

    @Override
    public void releaseAssignment(ReleaseAssignmentCommand assignment) {

    }

    @Override
    public ConsumerRegistration registerConsumer(RegisterConsumerCommand command) {
        return null;
    }

    @Override
    public void unregisterConsumer(UnregisterConsumerCommand command) {

    }

    @Override
    public void createPartition(CreatePartitionCommand command) {

    }

    @Override
    public void updateStatus(UpdateWorkersStatusCommand workerStatus) {

    }

    @Override
    public List<PartitionInfo> getPartitions() {
        return null;
    }

    @Override
    public PartitionConfiguration getPartitionConfiguration(String partitionKey) {
        return null;
    }

    @Override
    public void close() {

    }
}
