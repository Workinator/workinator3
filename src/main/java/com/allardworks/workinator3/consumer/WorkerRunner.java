package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.*;
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
    private final WorkerStatus status;
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


    /**
     * Terminate the worker.
     */
    void close(){
        try {
            worker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        workinator.releaseAssignment(status.getCurrentAssignment());
    }
}
