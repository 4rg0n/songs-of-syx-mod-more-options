package com.github.argon.sos.moreoptions.init;

/**
 * Contains different initialization phases to implement.
 * Some game elements are unavailable until a certain phase.
 */
public interface InitPhases {
    /**
     * When the game UI is loaded
     */
    void initGamePresent();

    /**
     * When the game starts the update() process
     */
    void initGameRunning();

    /**
     * Before a game is loaded
     */
    void initBeforeGameCreated();

    /**
     * When the mod instance is created
     */
    void initCreateInstance();
}
