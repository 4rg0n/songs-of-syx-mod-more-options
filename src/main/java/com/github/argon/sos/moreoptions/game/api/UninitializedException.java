package com.github.argon.sos.moreoptions.game.api;

public class UninitializedException extends RuntimeException {
    public UninitializedException() {
        this("Not initialized");
    }

    public UninitializedException(String message) {
        super(message);
    }
}
