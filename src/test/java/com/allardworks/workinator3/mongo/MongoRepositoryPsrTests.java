package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.mongo.testsupport.MongoRepositoryTester;
import com.allardworks.workinator3.psr.RepositoryPsrTests;
import com.allardworks.workinator3.testsupport.RepositoryTester;

public class MongoRepositoryPsrTests extends RepositoryPsrTests {
    @Override
    public RepositoryTester getRepoTester() {
        return new MongoRepositoryTester();
    }
}
