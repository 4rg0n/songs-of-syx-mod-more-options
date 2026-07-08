package com.github.argon.sos.mod.sdk.phase;

import org.jetbrains.annotations.Nullable;

/**
 * Used when something isn't initialized or hasn't a value yet,
 * because the {@link Phase} wasn't executed by the game yet.
 */
public class UninitializedException extends RuntimeException {
    /**
     * Creates a new {@link UninitializedException} with a default "Not initialized" message.
     */
    public UninitializedException() {
        this("Not initialized");
    }

    /**
     * @see RuntimeException#RuntimeException()
     */
    public UninitializedException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link UninitializedException} for the given {@link Phase}.
     *
     * @param phase which is required to be executed
     */
    public UninitializedException(Phase phase) {
        this(phase, null);
    }

    /**
     * Creates a new {@link UninitializedException} for the given {@link Phase}.
     *
     * @param phase which is required to be executed
     * @param message optional message
     */
    public UninitializedException(Phase phase, @Nullable String message) {
        super("Available in or after phase: " + phase + ((message == null || message.isEmpty()) ? "" : "\n" + message));
    }
}
