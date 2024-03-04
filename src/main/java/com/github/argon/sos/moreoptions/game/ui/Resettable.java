package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;

/**
 * Used to reset the state of an element
 */
public interface Resettable<Element> {
    default void reset() {};

    default void resetAction(Action<Element> resetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
