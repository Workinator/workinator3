package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.ConsumerRegistration;
import lombok.Data;

/**
 * Created by jaya on 2/27/18.
 * k?
 */
@Data
public class UnregisterConsumerCommand {
    private final ConsumerRegistration registration;
}
