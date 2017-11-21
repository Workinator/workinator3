package com.allardworks.workinator3.contracts;

public interface ExecutorSupplier {
    Service create(Worker worker);
}
