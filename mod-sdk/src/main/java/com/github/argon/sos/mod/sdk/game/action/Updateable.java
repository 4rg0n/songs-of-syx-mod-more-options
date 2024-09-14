package com.github.argon.sos.mod.sdk.game.action;

/**
 * For hooking something up to the games update loop
 */
public interface Updateable {
    void update(double seconds);
}
