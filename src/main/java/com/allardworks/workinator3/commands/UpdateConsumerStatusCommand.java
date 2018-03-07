package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.ConsumerRegistration;
import com.allardworks.workinator3.contracts.ConsumerStatus;
import lombok.Builder;
import lombok.Data;

/**
 * Created by jaya on 3/6/18.
 * k?
 */
@Data
@Builder
public class UpdateConsumerStatusCommand {
    private final ConsumerRegistration registration;
    private final ConsumerStatus status;
}
