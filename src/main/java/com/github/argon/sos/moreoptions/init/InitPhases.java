package com.github.argon.sos.moreoptions.init;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;

/**
 * Contains different initialization phases to implement.
 * Some game elements are unavailable until a certain phase.
 */
public interface InitPhases {
    /**
     * When the game UI is loaded
     */
    default void initGamePresent() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the game starts the update() process
     */
    default void initGameRunning() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * Before a game is loaded
     */
    default void initBeforeGameCreated() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the mod instance is created
     */
    default void initCreateInstance() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the game loaded a save game
     */
    default void initGameSaveLoaded(MoreOptionsConfig config) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
