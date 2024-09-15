package com.github.argon.sos.mod.sdk.json;

public class JsonException extends RuntimeException {
    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
