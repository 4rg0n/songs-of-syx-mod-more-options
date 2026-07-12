package com.github.argon.sos.mod.sdk.json;

/**
 * Exception thrown form within the json namespace.
 */
public class JsonException extends RuntimeException {

    /**
     * Creates a new {@link JsonException}.
     *
     * @see RuntimeException#RuntimeException()
     */
    public JsonException() {
    }

    /**
     * Creates a new {@link JsonException} with a message.
     *
     * @param message describing the exception
     * @see RuntimeException#RuntimeException(String)
     */
    public JsonException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link JsonException} with a message and cause.
     *
     * @param message describing the exception
     * @param cause of the exception
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
