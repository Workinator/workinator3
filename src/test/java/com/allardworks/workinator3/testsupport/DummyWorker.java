package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.Worker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.Data;

@Data
public class DummyWorker implements Worker {
    private WorkerContext lastContext;
    private long hitCount = 0;
    private boolean isFrozen;
    @Override
    public void execute(WorkerContext context) {
        lastContext = context;
        hitCount ++;
        while (isFrozen) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
