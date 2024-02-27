package com.github.argon.sos.moreoptions.game.ui;

/**
 * Used to show an element on the screen.
 *
 * @param <T> type of element passed to the onShow UIAction
 */
public interface Showable<T> {
    default void show() {};

     default void onShow(UIAction<T> showUIAction) {
         throw new UnsupportedOperationException("Method is not implemented");
     }
}
