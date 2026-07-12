package com.github.argon.sos.mod.sdk.game.api;

/**
 * Exception thrown from within the "game.api" namespace
 */
public class GameApiException extends Exception {
    /**
     * Creates a new {@link GameApiException} with a message.
     *
     * @param message describing the exception
     * @see Exception#Exception(String)
     */
    public GameApiException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link GameApiException} with a message and cause.
     *
     * @param message describing the exception
     * @param cause of the exception
     * @see Exception#Exception(String, Throwable)
     */
    public GameApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
