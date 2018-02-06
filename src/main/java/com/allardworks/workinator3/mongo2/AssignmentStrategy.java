package com.allardworks.workinator3.mongo2;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorStatus;

public interface AssignmentStrategy {
    Assignment getAssignment(ExecutorStatus executor);
    void releaseAssignment(Assignment assignment);

}
