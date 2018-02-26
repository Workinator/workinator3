package com.allardworks.workinator3;

import com.allardworks.workinator3.contracts.Workinator;

public interface WorkinatorTester extends AutoCloseable {
    Workinator getWorkinator();
    void setHasWork(String partitionKey, boolean hasMoreWork);
    void setDueDateFuture(String partitionKey);
}
