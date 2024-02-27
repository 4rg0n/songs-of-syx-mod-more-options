package com.github.argon.sos.moreoptions.game.ui;

/**
 * Used to refresh an element and its content.
 *
 * @param <T> type of element passed to the onRefresh UIAction
 */
public interface Refreshable<T> {

    default void refresh() {};

    default void onRefresh(UIAction<T> refreshUIAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
