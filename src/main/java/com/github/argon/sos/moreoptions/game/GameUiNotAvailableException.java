package com.github.argon.sos.moreoptions.game;

/**
 * Certain game UI elements are only available when the game is already created
 */
public class GameUiNotAvailableException extends RuntimeException {
    public GameUiNotAvailableException() {
    }

    public GameUiNotAvailableException(String message) {
        super(message);
    }

    public GameUiNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameUiNotAvailableException(Throwable cause) {
        super(cause);
    }
}
