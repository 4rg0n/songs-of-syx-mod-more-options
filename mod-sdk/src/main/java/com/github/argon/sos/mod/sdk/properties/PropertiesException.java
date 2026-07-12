package com.github.argon.sos.mod.sdk.properties;

/**
 * Thrown when reading or parsing Java properties fails.
 */
public class PropertiesException extends RuntimeException {
    /**
     * Creates a new {@link PropertiesException}.
     */
    public PropertiesException() {
    }

    /**
     * Creates a new {@link PropertiesException} with the given message.
     *
     * @param message describing the error
     */
    public PropertiesException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link PropertiesException} with the given message and cause.
     *
     * @param message describing the error
     * @param cause of the error
     */
    public PropertiesException(String message, Throwable cause) {
        super(message, cause);
    }
}
