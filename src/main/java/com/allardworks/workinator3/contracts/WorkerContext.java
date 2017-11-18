package com.allardworks.workinator3.contracts;

public interface WorkerContext {
    boolean canContinue();
    Assignment getAssignment();
    boolean getHasMoreWork();
    void setHasMoreWork(boolean hasMoreWork);
    void onStop(Runnable eventHandler);
}
