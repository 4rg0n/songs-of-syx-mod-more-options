package com.github.argon.sos.moreoptions.game.action;

/**
 * Used to refresh an element and its content.
 */
public interface Refreshable {

    default void refresh() {};

    default void refreshAction(VoidAction refreshAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
