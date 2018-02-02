package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.ConsumerRegistration;
import com.allardworks.workinator3.contracts.ExecutorId;
import com.allardworks.workinator3.mongo.MongoConfiguration;
import com.allardworks.workinator3.mongo.MongoDal;
import lombok.val;
import org.junit.Test;

public class Tests {
    @Test
    public void boo() {
        val create = CreatePartitionCommand.builder().partitionKey("a").maxIdleTimeSeconds(10).maxWorkerCount(10).build();
        val dal = new MongoDal(MongoConfiguration.builder().build());
        val workinator = new MongoWorkinator(dal);
        workinator.createPartition(create);

        val assignment = workinator.getAssignment(new ExecutorStatus(new ExecutorId(new ConsumerRegistration(new ConsumerId("")),1)));

    }
}
