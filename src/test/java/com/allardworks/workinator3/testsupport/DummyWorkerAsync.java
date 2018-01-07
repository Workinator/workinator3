package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.WorkerAsync;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.contracts.ExecutorId;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class DummyWorkerAsync implements WorkerAsync {
    private WorkerContext lastContext;
    private long hitCount = 0;
    private boolean isFrozen;
    private boolean thawOnStop = false;
    private final AtomicInteger contextStopEventHitCount = new AtomicInteger();
    private final AtomicInteger executeHitCount = new AtomicInteger();
    public boolean assigned;

    @Override
    public void execute(final WorkerContext context) {
        lastContext = context;
        assigned = true;

        executeHitCount.incrementAndGet();
        hitCount ++;
        while (isFrozen) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        context.setHasMoreWork(false);
    }

    @Override
    public void close() throws Exception {

    }
}
