package com.allardworks.workinator3.testsupport;

import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.WorkerContext;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class DummyAsyncWorker implements AsyncWorker {
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
        context.setHasWork(false);
    }

    @Override
    public void close() throws Exception {

    }
}
