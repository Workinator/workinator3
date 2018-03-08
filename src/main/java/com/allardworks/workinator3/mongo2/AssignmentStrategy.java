package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.WorkerStatus;

public interface AssignmentStrategy {
    Assignment getAssignment(WorkerStatus executor);
    void releaseAssignment(Assignment assignment);

}
