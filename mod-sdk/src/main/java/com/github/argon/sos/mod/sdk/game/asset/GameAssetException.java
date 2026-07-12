package com.github.argon.sos.mod.sdk.game.asset;

/**
 * Exception thrown from the game.asset namespace
 */
public class GameAssetException extends Exception {
    /**
     * Creates a new {@link GameAssetException}.
     *
     * @see Exception#Exception()
     */
    public GameAssetException() {}

    /**
     * Creates a new {@link GameAssetException} with a message.
     *
     * @param message describing the exception
     * @see Exception#Exception(String)
     */
    public GameAssetException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link GameAssetException} with a message and cause.
     *
     * @param message describing the exception
     * @param cause of the exception
     * @see Exception#Exception(String, Throwable)
     */
    public GameAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
