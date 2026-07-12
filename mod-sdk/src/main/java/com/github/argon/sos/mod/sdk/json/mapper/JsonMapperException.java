package com.github.argon.sos.mod.sdk.json.mapper;

/**
 * Exception thrown from within the "json.mapper" namespace.
 */
public class JsonMapperException extends RuntimeException {
    /**
     * Creates a new {@link JsonMapperException}.
     *
     * @see RuntimeException#RuntimeException()
     */
    public JsonMapperException() {
    }

    /**
     * Creates a new {@link JsonMapperException} with a message.
     *
     * @param message describing the error
     * @see RuntimeException#RuntimeException(String)
     */
    public JsonMapperException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link JsonMapperException} with a message and cause.
     *
     * @param message describing the error
     * @param cause of the error
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public JsonMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link JsonMapperException} with a cause.
     *
     * @param cause of the error
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public JsonMapperException(Throwable cause) {
        super(cause);
    }
}
