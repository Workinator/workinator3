package com.allardworks.workinator3.mongo;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.ExecutorId;

public interface RebalanceStrategy {
    Assignment getNextAssignment(ExecutorId executorId);
    void releaseAssignment(Assignment assignment);
}
