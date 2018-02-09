package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.WorkinatorTester;
import com.allardworks.workinator3.WorkinatorTests;
import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.ReleaseAssignmentCommand;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.ConsumerRegistration;
import com.allardworks.workinator3.contracts.WorkerId;
import com.allardworks.workinator3.contracts.WorkerStatus;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoWorkinatorTests extends WorkinatorTests {
    @Override
    protected WorkinatorTester getTester() {
        return new MongoWorkinatorTester();
    }

    private WorkerStatus createStatus(final String consumerId) {
        return new WorkerStatus(new WorkerId(new ConsumerRegistration(new ConsumerId(consumerId), ""), 1));
    }

    /**
     * Get the first due.
     * @throws Exception
     */
    @Test
    public void rule1() throws Exception {
        try (val tester = (MongoWorkinatorTester) getTester()) {
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
        try (val tester = (MongoWorkinatorTester) getTester()) {
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
     * Thus Rule4 will take effect: the first partion
     * @throws Exception
     */
    @Test
    public void rule4() throws Exception {
        try (val tester = (MongoWorkinatorTester) getTester()) {
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
        try (val tester = (MongoWorkinatorTester) getTester()) {
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
                // we'll get the same one back because rule 3 will see it has the fewest assignments

                workinator.releaseAssignment(new ReleaseAssignmentCommand(a2));
                val a7 = workinator.getAssignment(createStatus("consumer b"));
                assertEquals("b", a7.getPartitionKey());
                assertEquals("Rule 3", a7.getRuleName());
            }
        }
    }
}
