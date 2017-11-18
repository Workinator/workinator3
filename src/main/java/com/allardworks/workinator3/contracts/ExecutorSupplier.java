package com.allardworks.workinator3.contracts;

import java.util.List;

public interface ExecutorSupplier {
    Service create(WorkerId workerIds);
}
