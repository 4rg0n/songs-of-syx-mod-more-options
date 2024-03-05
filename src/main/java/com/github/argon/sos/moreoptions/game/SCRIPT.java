package com.github.argon.sos.moreoptions.game;

/**
 * Extends the game {@link script.SCRIPT} interface
 */
public interface SCRIPT extends script.SCRIPT {

    /**
     * Called by the games update loop
     */
    void update(double seconds);

    /**
     * Called when the game crashes
     */
    void crash(Throwable throwable);
}