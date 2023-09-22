package com.github.argon.sos.moreoptions.game;

public interface SCRIPT<T> extends script.SCRIPT {
    /**
     * Executed when the game is running
     */
    void initGameRunning();

    /**
     * Executed right after the game UI is present
     */
    void initGamePresent();

    /**
     * @param config nullable
     */
    void initGameSaveLoaded(T config);
}