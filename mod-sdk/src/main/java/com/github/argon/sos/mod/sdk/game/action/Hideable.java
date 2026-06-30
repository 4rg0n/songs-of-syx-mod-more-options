package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to hide an element on the screen.
 */
public interface Hideable {
    /**
     * Hides something. Usually an ui element
     */
    default void hide() {};

    /**
     * Optional action to be executed when hiding.
     *
     * @param hideAction to be executed when hiding
     */
    default void hideAction(VoidAction hideAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
