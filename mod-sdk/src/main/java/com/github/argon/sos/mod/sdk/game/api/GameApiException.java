package com.github.argon.sos.mod.sdk.game.api;

/**
 * Exception thrown from within the game api namespace
 */
public class GameApiException extends Exception {
    /**
     * @see Exception#Exception(String)
     */
    public GameApiException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public GameApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
