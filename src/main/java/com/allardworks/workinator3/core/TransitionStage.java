package com.allardworks.workinator3.core;

public enum TransitionStage {
    BeforeTransition {
        @Override
        public boolean isBefore() {
            return true;
        }
    },
    AfterTransition {
        @Override
        public boolean isAfter() {
            return true;
        }
    };

    public boolean isBefore() {
        return false;
    }

    public boolean isAfter() {
        return false;
    }
}
