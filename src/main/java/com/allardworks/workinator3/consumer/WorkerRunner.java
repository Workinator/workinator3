package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.contracts.Workinator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Runs the worker until it is time for the worker to stop.
 */
@RequiredArgsConstructor
@Getter
@Slf4j
class WorkerRunner {
    private final Workinator workinator;
    private final Assignment assignment;
    private final AsyncWorker worker;
    private final WorkerContext context;

    /**
     * A loop that executes the worker until
     * - coordinator says stop
     * - or, the worker reports there isn't any more work.
     */
    void run() {
        while (context.canContinue()) {
            try {
                worker.execute(context);
                if (!context.getHasMoreWork()) {
                    break;
                }
            } catch (final Exception ex) {
                log.error("worker.execute", ex);
            }
        }
    }

    void close(){
        try {
            worker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        workinator.releaseAssignment(assignment);
    }
}
