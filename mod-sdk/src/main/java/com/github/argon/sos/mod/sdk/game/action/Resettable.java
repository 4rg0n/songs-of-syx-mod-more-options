package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to reset the state of an element
 */
public interface Resettable {
    default void reset() {};

    default void resetAction(VoidAction resetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
