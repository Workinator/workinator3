package com.allardworks.workinator3.core.commands;

import com.allardworks.workinator3.core.Assignment;
import lombok.Data;

@Data
public class ReleaseAssignmentCommand {
    private final Assignment assignment;
}
