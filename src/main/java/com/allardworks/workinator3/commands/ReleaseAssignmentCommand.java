package com.allardworks.workinator3.commands;

import com.allardworks.workinator3.contracts.Assignment;
import lombok.Data;

@Data
public class ReleaseAssignmentCommand {
    private final Assignment assignment;
}
