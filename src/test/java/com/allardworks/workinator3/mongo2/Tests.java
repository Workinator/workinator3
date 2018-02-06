package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.commands.CreatePartitionCommand;
import com.allardworks.workinator3.contracts.*;
import com.allardworks.workinator3.testsupport.TimedActivity;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.IndexOptions;
import lombok.val;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Tests {
    @Test
    public void boo() throws Exception {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);

        val dal = new MongoDal(MongoConfiguration.builder().databaseName("test").build());
        val workinator = new MongoWorkinator(dal, new WhatsNextAssignmentStrategy(dal));
        val assignments = new ArrayList<Assignment>();

        val partitionCount = 1;

        try (val timer = new TimedActivity("create partitions")) {
            for (int i = 0; i < partitionCount; i++) {
                val create = CreatePartitionCommand.builder().partitionKey("p-" + i).maxIdleTimeSeconds(10).maxWorkerCount(10).build();
                workinator.createPartition(create);
            }
        }

        try (val timer = new TimedActivity("create")) {
            for (int i = 0; i < partitionCount; i++) {
                val assignment = workinator.getAssignment(new ExecutorStatus(new ExecutorId(new ConsumerRegistration(new ConsumerId("ca"), ""), 1)));
                assignments.add(assignment);

                workinator.releaseAssignment(assignment);
            }
        }

        try (val timer = new TimedActivity("release")) {
            for (val a : assignments) {
                workinator.releaseAssignment(a);
            }
        }
    }
}
