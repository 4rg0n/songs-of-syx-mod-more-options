package com.github.argon.sos.mod.sdk.phase;

public class UninitializedException extends RuntimeException {
    public UninitializedException() {
        this("Not initialized");
    }

    public UninitializedException(String message) {
        super(message);
    }

    public UninitializedException(Phase phase) {
        super("Available in or after phase " + phase);
    }
}
