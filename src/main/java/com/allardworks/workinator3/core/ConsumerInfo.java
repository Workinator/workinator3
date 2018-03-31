package com.allardworks.workinator3.core;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class ConsumerInfo {
    private final String name;
    private final Date connectedDate;
    private final int maxWorkerCount;
    private final List<ConsumerWorkerInfo> workers;
}
