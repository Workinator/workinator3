package com.allardworks.workinator3;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.commands.UnregisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class WorkinatorTests {
    protected abstract WorkinatorTester getTester();

    private WorkerStatus createStatus(final String consumerId) {
        return new WorkerStatus(new WorkerId(new ConsumerRegistration(new ConsumerId(consumerId), ""), 1));
    }

    @Test
    public void doNotExceedMaxWorkerCount() throws Exception {
        val testSize = 10;

        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {

                val partition = CreatePartitionCommand.builder().partitionKey("yadda").maxWorkerCount(testSize).build();
                workinator.createPartition(partition);

                val register = RegisterConsumerCommand.builder().id(new ConsumerId("smashing")).build();
                val registration = workinator.registerConsumer(register);

                tester.setHasWork("yadda", true);

                // will work for up to testSize.
                for (int i = 0; i < testSize; i++) {
                    val worker = new WorkerStatus(new WorkerId(registration, i));
                    val assignment = workinator.getAssignment(worker);
                    System.out.println(i);
                    assertEquals("yadda", assignment.getPartitionKey());
                }

                //next one will fail.
                val finalWorker = new WorkerStatus(new WorkerId(registration, testSize + 1));
                val finalAssignment = workinator.getAssignment(finalWorker);
                assertNull(finalAssignment);
            }
        }
    }

    @Test
    public void RULE2_getsSameAssignmentIfNothingElseAvailable() throws Exception {
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
    public void registerAndUnregisterConsumer() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val registerCommand = RegisterConsumerCommand.builder().id(new ConsumerId("boo")).build();

                // this works because not already registered
                val registration = workinator.registerConsumer(registerCommand);

                //  now it fails because already registered
                try {
                    workinator.registerConsumer(registerCommand);
                    Assert.fail("should've failed");
                } catch (final ConsumerExistsException ex) {
                    assertEquals("boo", ex.getConsumerId());
                }

                // unregister
                workinator.unregisterConsumer(new UnregisterConsumerCommand(registration));

                // now can register again
                workinator.registerConsumer(registerCommand);
            }
        }
    }

    /**
     * Get the first due.
     * @throws Exception
     */
    @Test
    public void rule1() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val par1 = CreatePartitionCommand.builder().partitionKey("a").maxWorkerCount(5).build();
                workinator.createPartition(par1);

                val par2 = CreatePartitionCommand.builder().partitionKey("b").maxWorkerCount(5).build();
                workinator.createPartition(par2);

                val par3 = CreatePartitionCommand.builder().partitionKey("c").maxWorkerCount(5).build();
                workinator.createPartition(par3);

                tester.setDueDateFuture("a");
                tester.setDueDateFuture("c");

                val a1 = workinator.getAssignment(createStatus("zz"));
                assertEquals("b", a1.getPartitionKey());
                assertEquals("Rule 1", a1.getRuleName());
            }
        }
    }

    /**
     * Get the first that is known to have work.
     * @throws Exception
     */
    @Test
    public void rule3() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val par1 = CreatePartitionCommand.builder().partitionKey("a").maxWorkerCount(5).build();
                workinator.createPartition(par1);

                val par2 = CreatePartitionCommand.builder().partitionKey("b").maxWorkerCount(5).build();
                workinator.createPartition(par2);

                val par3 = CreatePartitionCommand.builder().partitionKey("c").maxWorkerCount(5).build();
                workinator.createPartition(par3);

                tester.setDueDateFuture("a");
                tester.setHasWork("a", false);
                tester.setDueDateFuture("b");
                tester.setHasWork("b", true);
                tester.setDueDateFuture("c");
                tester.setHasWork("c", false);

                val a1 = workinator.getAssignment(createStatus("zz"));
                assertEquals("b", a1.getPartitionKey());
                assertEquals("Rule 3", a1.getRuleName());
                tester.setHasWork("b", false);

                val a2 = workinator.getAssignment(createStatus("zz"));
                assertEquals("a", a2.getPartitionKey());
                assertEquals("Rule 4", a2.getRuleName());

                val a3 = workinator.getAssignment(createStatus("zz"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals("Rule 4", a3.getRuleName());}
        }
    }

    /**
     * None of the partitions are due, and none have work.
     * Thus Rule4 will take effect: the first partition
     * @throws Exception
     */
    @Test
    public void rule4() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val par1 = CreatePartitionCommand.builder().partitionKey("a").maxWorkerCount(5).build();
                workinator.createPartition(par1);

                val par2 = CreatePartitionCommand.builder().partitionKey("b").maxWorkerCount(5).build();
                workinator.createPartition(par2);

                val par3 = CreatePartitionCommand.builder().partitionKey("c").maxWorkerCount(5).build();
                workinator.createPartition(par3);

                tester.setDueDateFuture("a");
                tester.setDueDateFuture("b");
                tester.setDueDateFuture("c");

                val a1 = workinator.getAssignment(createStatus("zz"));
                assertEquals("a", a1.getPartitionKey());
                assertEquals("Rule 4", a1.getRuleName());

                val a2 = workinator.getAssignment(createStatus("zz"));
                assertEquals("b", a2.getPartitionKey());
                assertEquals("Rule 4", a2.getRuleName());

                val a3 = workinator.getAssignment(createStatus("zz"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals("Rule 4", a3.getRuleName());}
        }
    }

    @Test
    public void rules1and3() throws Exception {
        try (val tester = getTester()) {
            try (val workinator = tester.getWorkinator()) {
                val par1 = CreatePartitionCommand.builder().partitionKey("a").maxWorkerCount(5).build();
                workinator.createPartition(par1);

                val par2 = CreatePartitionCommand.builder().partitionKey("b").maxWorkerCount(5).build();
                workinator.createPartition(par2);

                val par3 = CreatePartitionCommand.builder().partitionKey("c").maxWorkerCount(5).build();
                workinator.createPartition(par3);

                // 3 partitions. first 3 assignment all get the IsDue.

                val a1 = workinator.getAssignment(createStatus("consumer a"));
                assertEquals("a", a1.getPartitionKey());
                assertEquals("Rule 1", a1.getRuleName());

                val a2 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a2.getPartitionKey());
                assertEquals("Rule 1", a2.getRuleName());

                val a3 = workinator.getAssignment(createStatus("consumer c"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals("Rule 1", a3.getRuleName());

                // -----------------------------------------------------------------
                // now we'll start getting rule 3. Rule 3
                // assigns threads to partitions that are being worked on and
                // can support more threads.
                // -----------------------------------------------------------------

                // set the partitions to haswork=true so that rule 4 takes effect.
                tester.setHasWork("a", true);
                tester.setHasWork("b", true);
                tester.setHasWork("c", true);

                val a4 = workinator.getAssignment(createStatus("consumer a"));
                assertEquals("a", a4.getPartitionKey());
                assertEquals("Rule 3", a4.getRuleName());

                val a5 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a5.getPartitionKey());
                assertEquals("Rule 3", a5.getRuleName());

                val a6 = workinator.getAssignment(createStatus("consumer c"));
                assertEquals("c", a6.getPartitionKey());
                assertEquals("Rule 3", a6.getRuleName());

                // release one, then get an assignment
                // we'll get the same one back because rule 3 will see it has the fewest workers

                workinator.releaseAssignment(new ReleaseAssignmentCommand(a2));
                val a7 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a7.getPartitionKey());
                assertEquals("Rule 3", a7.getRuleName());
            }
        }
    }
}
