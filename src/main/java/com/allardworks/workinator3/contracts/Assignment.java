package com.allardworks.workinator3.contracts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;


@RequiredArgsConstructor
@Getter
public class Assignment {
    private final WorkerId workerId;
    private final String partitionKey;
    private final String receipt;
    private final String ruleName;

    private final Date assignmentDate = new Date();
    /**
     * Returns a new assignment object with all of the same information
     * except for rule name.
     * @param newRuleName
     * @return
     */
    public Assignment setRule(final String newRuleName) {
        return new Assignment(workerId, partitionKey, receipt, newRuleName);
    }

    public Assignment setWorkerId(final WorkerId workerId) {
        return new Assignment(workerId, partitionKey, receipt, ruleName);
    }
}
