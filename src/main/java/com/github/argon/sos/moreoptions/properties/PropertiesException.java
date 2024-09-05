package com.github.argon.sos.moreoptions.properties;

public class PropertiesException extends RuntimeException {
    public PropertiesException() {
    }

    public PropertiesException(String message) {
        super(message);
    }

    public PropertiesException(String message, Throwable cause) {
        super(message, cause);
    }
}
