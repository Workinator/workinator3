package com.allardworks.workinator3.contracts;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class ExecutorId {
    private final ConsumerRegistration consumer;
    private final int executorNumber;

    public String getAssignee() {
        return consumer.getConsumerId().getName() + ", #" + executorNumber;
    }
}
