package com.github.argon.sos.moreoptions.game;

public interface SCRIPT<Config> extends script.SCRIPT {
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
    void initGameSaveLoaded(Config config);

    void update(double seconds);

    void crash(Throwable throwable);
}