package com.allardworks.workinator3.contracts;

import java.util.function.Consumer;

public interface WorkerContext {

    boolean canContinue();
    Assignment getAssignment();
    boolean getHasMoreWork();
    void setHasMoreWork(boolean hasMoreWork);
}
