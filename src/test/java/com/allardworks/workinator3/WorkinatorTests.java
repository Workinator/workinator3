package com.allardworks.workinator3;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.commands.UnregisterConsumerCommand;
import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.WorkinatorTestHarness;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import static com.allardworks.workinator3.mongo2.WhatsNextAssignmentStrategy.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/*

Use the WORKINATOR TEST HARNESS, as demonstrated in the A WHOLE BUNCH OF STUFF test.

 */

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
    public void RULE2_getsSameAssignmentIfNothingElseAvailable_HasWork() throws Exception {
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
                assertEquals(RULE1, assignment1.getRuleName());

                // nothing is due, and current assignment has work,
                // so keep with current assignment.
                worker1.setHasWork(true);
                val assignment2 = workinator.getAssignment(worker1);
                assertEquals(RULE2, assignment2.getRuleName());
                assertEquals(assignment1.getPartitionKey(), assignment2.getPartitionKey());
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
     *
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
                assertEquals(RULE1, a1.getRuleName());
            }
        }
    }

    /**
     * Get the first that is known to have work.
     *
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

                // a and c have work.b does not.
                // b will be the first one to get picked up.
                //tester.setDueDateFuture("a");
                tester.setHasWork("a", false);
                tester.setDueDateFuture("b");
                tester.setHasWork("b", true);
                tester.setDueDateFuture("c");
                tester.setHasWork("c", false);

                // TODO: fails because A has workercount=1
                // need to fix that
                val a1 = workinator.getAssignment(createStatus("zz"));
                assertEquals(RULE3, a1.getRuleName());
                assertEquals("b", a1.getPartitionKey());
                tester.setHasWork("b", false);

                val a2 = workinator.getAssignment(createStatus("zz"));
                assertEquals(RULE4, a2.getRuleName());
                assertEquals("a", a2.getPartitionKey());

                val a3 = workinator.getAssignment(createStatus("zz"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals(RULE4, a3.getRuleName());
            }
        }
    }

    /**
     * None of the partitions are due, and none have work.
     * Thus Rule4 will take effect: the first partition
     *
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
                assertEquals(RULE4, a1.getRuleName());

                val a2 = workinator.getAssignment(createStatus("zz"));
                assertEquals("b", a2.getPartitionKey());
                assertEquals(RULE4, a2.getRuleName());

                val a3 = workinator.getAssignment(createStatus("zz"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals(RULE4, a3.getRuleName());
            }
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
                assertEquals(RULE1, a1.getRuleName());

                val a2 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a2.getPartitionKey());
                assertEquals(RULE1, a2.getRuleName());

                val a3 = workinator.getAssignment(createStatus("consumer c"));
                assertEquals("c", a3.getPartitionKey());
                assertEquals(RULE1, a3.getRuleName());

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
                assertEquals(RULE3, a4.getRuleName());

                val a5 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a5.getPartitionKey());
                assertEquals(RULE3, a5.getRuleName());

                val a6 = workinator.getAssignment(createStatus("consumer c"));
                assertEquals("c", a6.getPartitionKey());
                assertEquals(RULE3, a6.getRuleName());

                // release one, then get an assignment
                // we'll get the same one back because rule 3 will see it has the fewest workers
                workinator.releaseAssignment(new ReleaseAssignmentCommand(a2));
                val a7 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a7.getPartitionKey());
                assertEquals(RULE3, a7.getRuleName());
            }
        }
    }

    @Test
    public void RULE3_multipleConcurrency() throws Exception {
        try (val tester = new WorkinatorTestHarness(getTester())) {
            tester
                    // setup the partition and 4 workers
                    .createPartition("aaa", 3)
                    .createWorker("worker a")
                    .createWorker("worker b")
                    .createWorker("worker c")
                    .createWorker("worker d")

                    // get worker a then save it.
                    // this will udpate the partition with hasWork=true, which is necessary
                    // in order for subsequent workers to be assigned to the same partition.
                    .assertGetAssignment("worker a", "aaa", RULE1)
                    .setWorkerHasWork("worker a")
                    .saveWorkersStatus()

                    // b and c will be assigned to the same partition.
                    .assertGetAssignment("worker b", "aaa", RULE3)
                    .assertGetAssignment("worker c", "aaa", RULE3)

                    // max concurrency reached.
                    // next worker won't get an assignment.
                    .assertNullAssignment("worker d");
        }
    }

    @Test
    public void RULE3_MultipleConcurrency() throws Exception {
        try (val tester = new WorkinatorTestHarness(getTester())) {
            tester
                    // setup the partition and 4 workers
                    .createPartition("aaa", 3)
                    .createPartition("bbb", 3)
                    .setPartitionHasWork("aaa")
                    .setPartitionHasWork("bbb")
                    .createWorker("worker a")
                    .createWorker("worker b")
                    .createWorker("worker c")
                    .createWorker("worker d")
                    .createWorker("worker e")
                    .createWorker("worker f")
                    .createWorker("worker g")

                    .assertGetAssignment("worker a", "aaa", RULE1)
                    .assertGetAssignment("worker b", "bbb", RULE1)
                    .assertGetAssignment("worker c", "aaa", RULE3)
                    .assertGetAssignment("worker d", "bbb", RULE3)
                    .assertGetAssignment("worker e", "aaa", RULE3)
                    .assertGetAssignment("worker f", "bbb", RULE3)

                    // max concurrency reached for both partitions
                    // next worker won't get an assignment.
                    .assertNullAssignment("worker g");
        }
    }

    /**
     * New partitions always get priority.
     * @throws Exception
     */
    @Test
    public void RULE3_MultipleConcurrencyAcrossPartitions_TrumpedByRule1() throws Exception {
        try (val tester = new WorkinatorTestHarness(getTester())) {
            tester
                    // setup the partition and 4 workers
                    .createPartition("aaa", 3)
                    .createPartition("bbb", 3)
                    .setPartitionHasWork("aaa")
                    .setPartitionHasWork("bbb")
                    .createWorker("worker a")
                    .createWorker("worker b")
                    .createWorker("worker c")
                    .createWorker("worker d")
                    .createWorker("worker e")
                    .createWorker("worker f")
                    .createWorker("worker g")

                    // assignments will alternate
                    .assertGetAssignment("worker a", "aaa", RULE1)
                    .assertGetAssignment("worker b", "bbb", RULE1)
                    .assertGetAssignment("worker c", "aaa", RULE3)
                    .assertGetAssignment("worker d", "bbb", RULE3)

                    // now create a new partition. it will get priority.
                    .createPartition("ccc")
                    .assertGetAssignment("worker e", "ccc", RULE1)

                    // now back to the others, which have work and multiple workeres
                    .assertGetAssignment("worker f", "aaa", RULE3)
                    .assertGetAssignment("worker g", "bbb", RULE3);
        }
    }

    @Test
    public void AWholeBunchOfStuff() throws Exception {
        try (val tester = new WorkinatorTestHarness(getTester())) {
            tester
                    // create 3 partitions
                    .createPartition("a")
                    .createPartition("b")
                    .createPartition("c")

                    // ---------------------------------------------
                    // Rule 1
                    // ---------------------------------------------

                    // partitions will be returned in the order created
                    .createWorker("worker a")
                    .assertGetAssignment("worker a", "a", RULE1)
                    .createWorker("worker b")
                    .assertGetAssignment("worker b", "b", RULE1)
                    .createWorker("worker c")
                    .assertGetAssignment("worker c", "c", RULE1)

                    // nothing for the 4th worker to do.
                    // all partitions have max concurrency = 1, and there are now more workers than partitions
                    .createWorker("worker d")
                    .assertNullAssignment("worker d")

                    // release the worker b
                    .releaseAssignment("worker b")

                    // ---------------------------------------------
                    // RULE 4
                    // ---------------------------------------------
                    // now d will get it
                    // RULE 4 because it's not due and doesn't have work.
                    // RULE 4 is first partition where worker count = 0
                    .assertGetAssignment("worker d", "b", RULE4)

                    // ---------------------------------------------
                    // RULE 2
                    // ---------------------------------------------
                    // a has work, and there aren't any partitions due
                    // getting assignment will result in rule 2
                    .setWorkerHasWork("worker a")
                    .assertGetAssignment("worker a", "a", RULE2)

                    // ---------------------------------------------
                    // RULE 5
                    // ---------------------------------------------
                    // c doesn't have work, and there aren't any partitions due.
                    // RULE2 and RULE3 are for when there is work. there isn't.
                    // RULE4 won't find this partition because it's still assigned.
                    // RULE5 will prevail... nothing better to do, so do what you're doing
                    // even though there isn't work.
                    .setWorkerDoesntHaveWork("worker c")
                    .assertGetAssignment("worker c", "c", RULE5);
        }
    }
}