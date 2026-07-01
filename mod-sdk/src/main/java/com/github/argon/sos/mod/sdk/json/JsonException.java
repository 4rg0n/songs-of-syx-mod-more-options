package com.github.argon.sos.mod.sdk.json;

/**
 * Exception thrown form within the json namespace.
 */
public class JsonException extends RuntimeException {

    /**
     * @see RuntimeException#RuntimeException()
     */
    public JsonException() {
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public JsonException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
