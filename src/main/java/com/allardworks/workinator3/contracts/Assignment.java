package com.allardworks.workinator3.contracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@Getter
public class Assignment {
    // TODO: this is a problem in RunnerProvider. need to stabilize this.
    // revisit id and status objects.
    // could get rid of it, but RELEASE uses it.
    private final WorkerId workerId;
    private final String partitionKey;
    private final String receipt;
    private final String ruleName;

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
