package com.github.argon.sos.mod.sdk.game.data;

public class GameDataException extends Exception {
    public GameDataException() {
    }

    public GameDataException(String message) {
        super(message);
    }

    public GameDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
