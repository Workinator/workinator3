package com.allardworks.workinator3.contracts;

public interface Worker extends AutoCloseable {
    WorkerId getId();
}
