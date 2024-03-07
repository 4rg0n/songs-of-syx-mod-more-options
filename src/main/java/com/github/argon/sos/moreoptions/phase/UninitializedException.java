package com.github.argon.sos.moreoptions.phase;

public class UninitializedException extends RuntimeException {
    public UninitializedException() {
        this("Not initialized");
    }

    public UninitializedException(String message) {
        super(message);
    }
}
