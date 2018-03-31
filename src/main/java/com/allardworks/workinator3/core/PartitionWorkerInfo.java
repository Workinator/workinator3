package com.allardworks.workinator3.core;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class PartitionWorkerInfo {
    private final String assignee;
    private final Date createDate;
    private final String rule;
}
