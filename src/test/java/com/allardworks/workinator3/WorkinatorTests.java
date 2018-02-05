package com.allardworks.workinator3;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.commands.RegisterConsumerCommand;
import com.allardworks.workinator3.contracts.ConsumerExistsException;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public abstract class WorkinatorTests {
    protected abstract WorkinatorTester getTester();

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
                    Assert.assertEquals("abc", partition.getPartitionKey());
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
                    Assert.assertEquals("boo", ex.getConsumerId());
                }
            }
        }
    }
}
