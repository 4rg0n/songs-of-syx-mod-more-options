package com.github.argon.sos.moreoptions.game.action;

/**
 * Used to show an element on the screen.
 */
public interface Showable {
    default void show() {};

     default void showAction(VoidAction showAction) {
         throw new UnsupportedOperationException("Method is not implemented");
     }
}
