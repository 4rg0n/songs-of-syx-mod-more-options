package com.github.argon.sos.moreoptions.game.api;

public class UiException extends Exception {
    public UiException(String message) {
        super(message);
    }

    public UiException(String message, Throwable cause) {
        super(message, cause);
    }
}
