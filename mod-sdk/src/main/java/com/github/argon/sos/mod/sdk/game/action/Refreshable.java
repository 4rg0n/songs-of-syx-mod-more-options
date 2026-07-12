package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to refresh an element and its content.
 */
public interface Refreshable {

    /**
     * Refreshes the element
     */
    default void refresh() {};

    /**
     * Optional action to be executed when refreshing.
     *
     * @param refreshAction to be executed when refreshing
     */
    default void refreshAction(VoidAction refreshAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
