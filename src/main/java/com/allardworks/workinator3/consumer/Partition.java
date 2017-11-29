package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.ExecutorId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode
public class Partition {
    private final String partitionKey;
    private boolean hasMoreWork;
    private LocalTime lastWork;
    private LocalTime lastWorkCheck;
    private long workCount;
    private int maxWorkerCount = 1;
    private ExecutorId assignee;

    /**
     * Indicates if there is more work to do.
     * When a worker is told to stop working, it may know that there
     * is more work to do. The workinator uses this information to help prioritize.
     * @param hasMoreWork
     * @return
     */
    public Partition setMoreWork(final boolean hasMoreWork) {
        this.hasMoreWork = hasMoreWork;
        return this;
    }

    public Partition noMoreWork() {
        setMoreWork(false);
        return this;
    }

    public Partition hasMoreWork() {
        setMoreWork(false);
        return this;
    }

    /**
     * Indicate that the partition was checked for work.
     * It doesn't matter if work was done or not,
     * just indicating that it was checked.
     * @return
     */
    public Partition checkedForWork() {
        lastWorkCheck = LocalTime.now();
        return this;
    }

    public Partition didWork() {
        didWork(1);
        return this;
    }

    public Partition didWork(final int workCount) {
        lastWork = LocalTime.now();
        this.workCount += workCount;
        return this;
    }

    public Partition setMaxWorkerCount(final int maxWorkerCount) {
        // todo: validate
        this.maxWorkerCount = maxWorkerCount;
        return this;
    }

    public int getMaxWorkerCount() {
        return maxWorkerCount;
    }

    public ExecutorId getAssignee() {
        return assignee;
    }

    public Partition setAssignee(final ExecutorId assignee) {
        this.assignee = assignee;
        return this;
    }

}
