package com.allardworks.workinator3.contracts;

import com.allardworks.workinator3.contracts.ConsumerId;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class WorkerId {
    private final ConsumerRegistration consumer;
    private final int listenerNumber;
}
