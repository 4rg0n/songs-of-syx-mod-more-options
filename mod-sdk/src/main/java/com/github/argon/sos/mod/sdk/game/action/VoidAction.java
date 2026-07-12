package com.github.argon.sos.mod.sdk.game.action;

/**
 * A function accepting no parameter and with void as return
 */
@FunctionalInterface
public interface VoidAction {
    /**
     * Does nothing
     */
    void accept();
}
