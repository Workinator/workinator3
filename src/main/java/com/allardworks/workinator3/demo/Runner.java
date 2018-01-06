package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.WorkinatorAdmin;
import com.allardworks.workinator3.consumer.WorkinatorConsumer;
import com.allardworks.workinator3.consumer.WorkinatorConsumerFactory;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.CreatePartitionCommand;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

@Service
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {
    private final Map<String, WorkinatorConsumer> consumers = new HashMap<>();

    @Autowired
    private final WorkinatorAdmin admin;

    @Autowired
    private final WorkinatorConsumerFactory consumerFactory;

    /**
     * Create a partition.
     * @param command
     * @throws PartitionExistsException
     */
    private void createPartition(final CommandLine command) throws PartitionExistsException {
        val partitionName = command.getOptionValue("cp");
        if (partitionName == null){
            return;
        }

        val partition = CreatePartitionCommand
                .builder()
                .partitionKey(partitionName)
                .build();
        admin.createPartition(partition);
    }

    private void createConsumer(final CommandLine command) {
        val consumerName = command.getOptionValue("cc");
        if (consumerName == null) {
            return;
        }

        val id = new ConsumerId(consumerName);
        val consumer = consumerFactory.create(id);
        consumer.start();
        consumers.put(consumerName, consumer);
    }

    @Override
    public void run(String... strings) throws Exception {
        val parser = new DefaultParser();
        val options = new Options();
        options.addOption(new Option("cc", "createconsumer", true, "Create a consumer"));
        options.addOption(new Option("cp", "createpartition", true, "Create a partition"));


        while (true) {
            try {
                val command = parser.parse(options, getInput());
                createPartition(command);
                createConsumer(command);
            } catch (final Exception ex) {
                out.println("  Error: " + ex.getMessage());
            }
        }
    }

    private String[] getInput() {
        try {
            out.print("Workinator> ");
            return new BufferedReader(new InputStreamReader(System.in)).readLine().split(" ");
        } catch (IOException e) {
            return new String[]{};
        }
    }
}
