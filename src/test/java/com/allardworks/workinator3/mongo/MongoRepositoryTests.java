package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.mongo.testsupport.MongoRepositoryTester;
import com.allardworks.workinator3.repository.RepositoryTests;
import com.allardworks.workinator3.testsupport.RepositoryTester;

public class MongoRepositoryTests extends RepositoryTests {
    @Override
    public RepositoryTester getRepoTester() {
        return new MongoRepositoryTester();
    }
}
