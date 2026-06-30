package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to reset the state of a component
 */
public interface Resettable {
    /**
     * Resets values and / or state of a component.
     */
    default void reset() {};

    /**
     * Optional action to be executed when a component is reset.
     *
     * @param resetAction to be executed when resetting
     */
    default void resetAction(VoidAction resetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
