package com.github.argon.sos.mod.sdk.game.asset;

/**
 * Exception thrown from the game.asset namespace
 */
public class GameAssetException extends Exception {
    /**
     * @see Exception#Exception()
     */
    public GameAssetException() {}

    /**
     * @see Exception#Exception(String)
     */
    public GameAssetException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public GameAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}
