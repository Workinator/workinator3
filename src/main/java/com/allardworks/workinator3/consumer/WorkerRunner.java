package com.allardworks.workinator3.consumer;

import com.allardworks.workinator3.contracts.Assignment;
import com.allardworks.workinator3.contracts.AsyncWorker;
import com.allardworks.workinator3.contracts.WorkerContext;
import com.allardworks.workinator3.contracts.WorkinatorRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
class WorkerRunner {
    private final WorkinatorRepository repo;
    private final Assignment assignment;
    private final AsyncWorker worker;
    private final WorkerContext context;

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
        repo.releaseAssignment(assignment);
    }
}
