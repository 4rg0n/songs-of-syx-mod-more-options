package com.github.argon.sos.moreoptions.game.ui;

/**
 * Used to reset the state of an element
 */
public interface Resettable<T> {
    default void reset() {};

    default void onReset(UIAction<T> resetUIAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
