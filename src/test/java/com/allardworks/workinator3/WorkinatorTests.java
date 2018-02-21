package com.allardworks.workinator3;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.contracts.*;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public abstract class WorkinatorTests {
    protected abstract WorkinatorTester getTester();

    @Test
    public void canOnlyGetPartitionOnce() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val partition = CreatePartitionCommand.builder().partitionKey("yadda").maxWorkerCount(1).build();
                workinator.createPartition(partition);

                val register = RegisterConsumerCommand.builder().id(new ConsumerId("smashing")).build();
                val registration = workinator.registerConsumer(register);

                val worker1 = new WorkerStatus(new WorkerId(registration, 1));
                val worker2 = new WorkerStatus(new WorkerId(registration, 2));
                val assignment1 = workinator.getAssignment(worker1);
                assertEquals("yadda", assignment1.getPartitionKey());

                val assignment2 = workinator.getAssignment(worker2);
                assertNull(assignment2);
            }
        }
    }

    @Test
    public void getsSameAssignmentIfNothingElseAvailable() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val partition = CreatePartitionCommand.builder().partitionKey("yadda").maxWorkerCount(1).build();
                workinator.createPartition(partition);

                val register = RegisterConsumerCommand.builder().id(new ConsumerId("smashing")).build();
                val registration = workinator.registerConsumer(register);

                val worker1 = new WorkerStatus(new WorkerId(registration, 1));
                val assignment1 = workinator.getAssignment(worker1);
                worker1.setCurrentAssignment(assignment1);
                assertEquals("yadda", assignment1.getPartitionKey());

                val assignment2 = workinator.getAssignment(worker1);
                assertEquals(assignment1, assignment2);
            }
        }
    }

    @Test
    public void canGetAssignmentAfterItIsReleased() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val partition = CreatePartitionCommand.builder().partitionKey("yadda").maxWorkerCount(1).build();
                workinator.createPartition(partition);

                val register = RegisterConsumerCommand.builder().id(new ConsumerId("smashing")).build();
                val registration = workinator.registerConsumer(register);

                val worker1 = new WorkerStatus(new WorkerId(registration, 1));
                val assignment1 = workinator.getAssignment(worker1);
                worker1.setCurrentAssignment(assignment1);
                assertEquals("yadda", assignment1.getPartitionKey());

                // one partition already assigned, so nothing to do here.
                val worker2 = new WorkerStatus(new WorkerId(registration, 2));
                val assignment2 = workinator.getAssignment(worker2);
                assertNull(assignment2);

                // release the partition, then another worker can get it
                workinator.releaseAssignment(new ReleaseAssignmentCommand(assignment1));
                val assignment3 = workinator.getAssignment(worker2);
                assertEquals("yadda", assignment3.getPartitionKey());
            }
        }
    }

    @Test
    public void partitionCanOnlyBeCreatedOnce() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val partition = CreatePartitionCommand.builder().partitionKey("abc").build();
                workinator.createPartition(partition);

                try {
                    workinator.createPartition(partition);
                    Assert.fail("should've failed");
                } catch (final PartitionExistsException ex) {
                    assertEquals("abc", partition.getPartitionKey());
                }
            }
        }
    }

    @Test
    public void consumerCanOnlyRegisterOnce() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val register = RegisterConsumerCommand.builder().id(new ConsumerId("boo")).build();
                workinator.registerConsumer(register);

                try {
                    workinator.registerConsumer(register);
                    Assert.fail("should've failed");
                } catch (final ConsumerExistsException ex) {
                    assertEquals("boo", ex.getConsumerId());
                }
            }
        }
    }
}
