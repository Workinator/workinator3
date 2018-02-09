package com.allardworks.workinator3.contracts;

import lombok.Data;

@Data
public class WorkerStatus {
    private final WorkerId workerId;

    private boolean hasWork;

    private Assignment currentAssignment;
}
