package com.allardworks.workinator3.core;

import lombok.Data;

/**
 * Describes a state change in a service.
 * The state change may be about to occur (pre),
 * of just occurred (post).
 */
@Data
public class Transition {
    private final TransitionStage stage;
    private final Status before;
    private final Status after;

    public boolean isPostStarting() {
        return stage.equals(TransitionStage.AfterTransition) && after.equals(Status.Starting);
    }

    public boolean isPostStarted() {
        return stage.equals(TransitionStage.AfterTransition) && after.equals(Status.Started);
    }

    public boolean isPostStopping() {
        return stage.equals(TransitionStage.AfterTransition) && after.equals(Status.Stopping);
    }

    public boolean isPostStopped() {
        return stage.equals(TransitionStage.AfterTransition) && after.equals(Status.Stopped);
    }}
