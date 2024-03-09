package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;

/**
 * Used to hide an element on the screen.
 *
 * @param <Element> type of element passed to the onHide Action
 */
public interface Hideable<Element> {
    default void hide() {};

     default void hideAction(Action<Element> hideAction) {
         throw new UnsupportedOperationException("Method is not implemented");
     }
}
