package com.github.argon.sos.mod.sdk.config;

/**
 * Exception thrown from within the "config" namespace.
 */
public class ConfigException extends RuntimeException {
    /**
     * Creates a new {@link ConfigException} with no message or cause.
     *
     * @see RuntimeException#RuntimeException()
     */
    public ConfigException() {
    }

    /**
     * Creates a new {@link ConfigException} with a message.
     *
     * @param message describing the problem
     * @see RuntimeException#RuntimeException(String)
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link ConfigException} with a message and cause.
     *
     * @param message describing the problem
     * @param cause of the exception
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
