package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.PartitionDto;
import com.allardworks.workinator3.mongo.testsupport.MongoRepositoryTester;
import com.allardworks.workinator3.repository.AdminRepositoryTests;
import com.allardworks.workinator3.testsupport.RepositoryTester;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoAdminRepositoryTests extends AdminRepositoryTests {
    @Override
    public RepositoryTester getRepoTester() {
        return new MongoRepositoryTester();
    }

    /**
     * Make sure the worker documents are created.
     * @throws Exception
     */
    @Test
    public void createWorkers() throws Exception {
        try (val tester = (MongoRepositoryTester) getRepoTester()) {
            val partition = new PartitionDto();
            partition.setMaxWorkerCount(18);
            partition.setPartitionKey("abc");
            tester.getAdminRepository().createPartition(partition);
            assertEquals(partition.getMaxWorkerCount(), tester.getWorkersCollection().count());
        }
    }
}
