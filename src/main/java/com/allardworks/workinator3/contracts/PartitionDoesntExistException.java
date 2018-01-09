package com.allardworks.workinator3.contracts;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PartitionDoesntExistException extends Exception {
    private final String partitionKey;
    public PartitionDoesntExistException(final String partitionKey) {
        super("The partition doesn't exist: " + partitionKey);
        this.partitionKey = partitionKey;
    }
}
