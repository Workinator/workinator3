package com.allardworks.workinator3.core;

public enum Status {
    Stopping,
    Stopped {
        @Override
        public boolean isStopped(){
            return true;
        }
    },
    Starting,
    Started {
        @Override
        public boolean isStarted() {
            return true;
        }
    };

    public boolean isStopped() {
        return false;
    }

    public boolean isStarted() {
        return false;
    }
}
