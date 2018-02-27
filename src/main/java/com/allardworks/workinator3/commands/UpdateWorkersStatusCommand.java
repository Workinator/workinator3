package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.WorkerStatus;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWorkersStatusCommand {
    private final List<WorkerStatus> status;
}
