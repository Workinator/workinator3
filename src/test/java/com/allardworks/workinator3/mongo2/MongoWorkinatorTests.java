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
}
