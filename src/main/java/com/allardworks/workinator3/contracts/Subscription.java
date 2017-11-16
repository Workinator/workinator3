package com.allardworks.workinator3.contracts;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Subscription {
    private final WorkerId workerId;
    private final String subscriptionToken;
}
