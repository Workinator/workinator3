package com.allardworks.workinator3.contracts;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConsumerExistsException extends Exception {
    private final String consumerName;
    public ConsumerExistsException(final String consumerName) {
        super("The consumer already exists: " + consumerName);
        this.consumerName = consumerName;
    }
}
