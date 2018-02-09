package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.WorkerStatus;
import lombok.Data;

@Data
public class UpdateWorkerStatusCommand {
    private final WorkerStatus status;
}
