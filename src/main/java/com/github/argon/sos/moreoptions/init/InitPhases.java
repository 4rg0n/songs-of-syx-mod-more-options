package com.github.argon.sos.moreoptions.init;

import java.nio.file.Path;

/**
 * Contains different initialization phases to implement.
 * Some game elements are unavailable until a certain phase.
 */
public interface InitPhases {
    /**
     * When the game UI is loaded
     */
    default void initGameUiPresent() {
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
    default void initGameSaveLoaded(Path saveFilePath) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the game saves
     */
    default void initGameSaved(Path saveFilePath) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When a new game session is started (not when a player starts a fresh new game)
     * This will not fire when the player loads from an existing game into another one.
     */
    default void initNewGameSession() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the player loads into game while already playing another one
     */
    default void initGameSaveReloaded() {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
