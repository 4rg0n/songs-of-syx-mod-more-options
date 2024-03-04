package com.github.argon.sos.moreoptions.game;

/**
 * Extends the game {@link script.SCRIPT} interface
 */
public interface SCRIPT extends script.SCRIPT {

    void update(double seconds);

    void crash(Throwable throwable);
}