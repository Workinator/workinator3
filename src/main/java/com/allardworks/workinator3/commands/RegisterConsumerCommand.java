package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.ConsumerId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterConsumerCommand {
    private final ConsumerId id;
    public final int maxWorkerCount;

    public static class RegisterConsumerCommandBuilder {
        private int maxWorkerCount = 1;
    }
}
