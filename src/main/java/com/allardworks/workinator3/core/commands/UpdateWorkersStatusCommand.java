package com.allardworks.workinator3.core.commands;

import com.allardworks.workinator3.core.WorkerStatus;
import lombok.Data;

import java.util.List;

@Data
public class UpdateWorkersStatusCommand {
    private final List<WorkerStatus> status;
}
