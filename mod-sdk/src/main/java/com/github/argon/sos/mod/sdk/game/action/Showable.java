package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to show an element on the screen.
 */
public interface Showable {
    /**
     * Displays a hidden ui element
     */
    default void show() {};

    /**
     * Optional action to be executed when an element is shown.
     *
     * @param showAction to be executed when shown
     */
    default void showAction(VoidAction showAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
