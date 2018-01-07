package com.allardworks.workinator3.demo;

import com.allardworks.workinator3.WorkinatorAdmin;
import com.allardworks.workinator3.consumer.WorkinatorConsumer;
import com.allardworks.workinator3.consumer.WorkinatorConsumerFactory;
import com.allardworks.workinator3.contracts.ConsumerId;
import com.allardworks.workinator3.contracts.CreatePartitionCommand;
import com.allardworks.workinator3.contracts.PartitionExistsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.cli.*;
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

    private final ObjectMapper mapper = new ObjectMapper();

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
    private boolean createPartition(final CommandLine command) throws PartitionExistsException {
        val partitionName = command.getOptionValue("cp");
        if (partitionName == null){
            return false;
        }

        val partition = CreatePartitionCommand
                .builder()
                .partitionKey(partitionName)
                .build();
        admin.createPartition(partition);
        return true;
    }

    private boolean createConsumer(final CommandLine command) {
        val consumerName = command.getOptionValue("cc");
        if (consumerName == null) {
            return false;
        }

        val id = new ConsumerId(consumerName);
        val consumer = consumerFactory.create(id);
        consumer.start();
        consumers.put(consumerName, consumer);
        return true;
    }

    private boolean showConsumerStatus(final CommandLine command) throws JsonProcessingException {
        if (!command.hasOption("cs")) {
            return false;
        }

        for (val c : consumers.values()) {
            out.println(mapper.writeValueAsString(c.getInfo()));
        }
        return true;
    }

    private void showHelp(final Options options) {
        val formatter = new HelpFormatter();
        formatter.printHelp( "workinator demo cli", options );
        out.println();
    }

    private boolean showHelp(final CommandLine command, final Options options) {
        if (!command.hasOption("help")){
            return false;
        }

        showHelp(options);
        return true;
    }

    @Override
    public void run(String... strings) throws Exception {
        val parser = new DefaultParser();
        val options = new Options();
        options.addOption(new Option("cc", "createconsumer", true, "Create a consumer"));
        options.addOption(new Option("cp", "createpartition", true, "Create a partition"));
        options.addOption(new Option("cs", "consumerstatus", false, "Display Consumer Status"));
        options.addOption(new Option("help", "help", false, "print this message"));

        while (true) {
            try {
                val command = parser.parse(options, getInput());
                val processed =
                createPartition(command)
                || createConsumer(command)
                || showConsumerStatus(command)
                || showHelp(command, options);

                if (!processed) {
                    showHelp(options);
                }
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
