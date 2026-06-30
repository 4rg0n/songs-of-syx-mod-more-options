package com.github.argon.sos.mod.sdk.game.action;

/**
 * For hooking something up to the games update loop
 */
public interface Updateable {
    /**
     * Executed when the game runs its update loop.
     *
     * @param seconds since the last update
     */
    void update(double seconds);
}
