package com.github.argon.sos.moreoptions.config;

public class ConfigException extends RuntimeException {
    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
