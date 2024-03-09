package com.github.argon.sos.moreoptions.phase;

import java.nio.file.Path;

/**
 * Contains different game phases to implement.
 * Some game elements are unavailable until a certain phase.
 *
 * Classes implementing a certain init method can be registered
 * via the {@link PhaseManager#register(Phase, Phases)} method.
 */
public interface Phases {
    /**
     * 1. PHASE
     * Before a game is loaded
     */
    default void initBeforeGameCreated() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * 2. PHASE
     * When the mod instance is created
     */
    default void initModCreateInstance() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * (3.) PHASE: can be called multiple times
     * When the game loaded a save game
     */
    default void onGameSaveLoaded(Path saveFilePath) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * (4.) PHASE: can be called multiple times
     * When the player loads into game while already playing another one
     */
    default void onGameSaveReloaded() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * 5. PHASE
     * When a new game session is started (not when a player starts a fresh new game)
     * This will not fire when the player loads from an existing game into another one.
     */
    default void initNewGameSession() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * 6. PHASE
     * When the game starts the update() process
     */
    default void initGameUpdating() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * (7.) PHASE: can be called multiple times
     * Called by the games update loop
     */
    default void onGameUpdate(double seconds)  {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * 8. PHASE
     * When the game UI is loaded
     */
    default void initGameUiPresent() {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the game saves
     */
    default void onGameSaved(Path saveFilePath) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * When the game crashes
     */
    default void onCrash(Throwable e) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
