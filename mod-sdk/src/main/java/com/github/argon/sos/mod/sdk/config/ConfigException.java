package com.github.argon.sos.mod.sdk.config;

/**
 * Exception thrown form within the config namespace.
 */
public class ConfigException extends RuntimeException {
    /**
     * @see RuntimeException#RuntimeException()
     */
    public ConfigException() {
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
