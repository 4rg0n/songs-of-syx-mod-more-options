package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;

/**
 * Used to show an element on the screen.
 *
 * @param <Element> type of element passed to the onShow UIAction
 */
public interface Showable<Element> {
    default void show() {};

     default void onShow(Action<Element> showAction) {
         throw new UnsupportedOperationException("Method is not implemented");
     }
}
