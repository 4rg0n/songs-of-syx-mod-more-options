package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;

/**
 * Used to refresh an element and its content.
 *
 * @param <Element> type of element passed to the onRefresh Action
 */
public interface Refreshable<Element> {

    default void refresh() {};

    default void refreshAction(Action<Element> refreshAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
