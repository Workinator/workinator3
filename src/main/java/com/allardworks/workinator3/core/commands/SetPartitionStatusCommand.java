package com.allardworks.workinator3.core.commands;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetPartitionStatusCommand {
    private final boolean hasWork;
    private final String partitionKey;
}
