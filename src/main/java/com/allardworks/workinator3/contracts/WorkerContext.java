package com.allardworks.workinator3.contracts;

public interface WorkerContext {
    boolean canContinue();
    Assignment getAssignment();
    void setHasMoreWork(boolean hasMoreWork);
}
