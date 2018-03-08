package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.WorkinatorTester;
import com.allardworks.workinator3.WorkinatorTests;

public class MongoWorkinatorTests extends WorkinatorTests {
    @Override
    protected WorkinatorTester getTester() {
        return new MongoWorkinatorTester();
    }
}
