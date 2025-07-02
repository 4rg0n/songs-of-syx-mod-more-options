package com.github.argon.sos.mod.sdk.phase;

import org.jetbrains.annotations.Nullable;

public class UninitializedException extends RuntimeException {
    public UninitializedException() {
        this("Not initialized");
    }

    public UninitializedException(String message) {
        super(message);
    }

    public UninitializedException(Phase phase) {
        this(phase, null);
    }

    public UninitializedException(Phase phase, @Nullable String message) {
        super("Available in or after phase: " + phase + ((message == null || message.isEmpty()) ? "" : "\n" + message));
    }
}
