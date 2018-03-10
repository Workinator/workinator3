package com.allardworks.workinator3.contracts;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class WorkerInfo {
    private final String assignee;
    private final Date createDate;
    private final String rule;
}
