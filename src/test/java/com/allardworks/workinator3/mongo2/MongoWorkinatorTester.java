package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.WorkinatorTester;
import com.allardworks.workinator3.contracts.Workinator;

public class MongoWorkinatorTester implements WorkinatorTester {
    private MongoDal dal;

    @Override
    public Workinator getWorkinator() {
        dal = new MongoDal(MongoConfiguration
                .builder()
                .databaseName("test")
                .build());
        return new MongoWorkinator(dal);
    }

    @Override
    public void close() throws Exception {
        dal.getDatabase().drop();
    }
}
