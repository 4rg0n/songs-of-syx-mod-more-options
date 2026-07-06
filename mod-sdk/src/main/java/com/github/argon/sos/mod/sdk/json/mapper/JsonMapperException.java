package com.github.argon.sos.mod.sdk.json.mapper;

/**
 * Exception thrown from within the "json.mapper" namespace.
 */
public class JsonMapperException extends RuntimeException {
    /**
     * @see RuntimeException#RuntimeException()
     */
    public JsonMapperException() {
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public JsonMapperException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public JsonMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public JsonMapperException(Throwable cause) {
        super(cause);
    }
}
