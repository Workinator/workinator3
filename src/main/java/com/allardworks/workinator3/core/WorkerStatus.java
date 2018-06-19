package com.allardworks.workinator3.core;

import lombok.Data;
import lombok.val;

@Data
public class WorkerStatus {
    private final WorkerId workerId;

    private Assignment currentAssignment;

    public WorkerStatus clone() {
        val copy = new WorkerStatus(workerId);
        copy.currentAssignment = currentAssignment;
        return copy;
    }
}
