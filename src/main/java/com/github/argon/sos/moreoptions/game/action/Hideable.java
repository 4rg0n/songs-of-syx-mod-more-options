package com.github.argon.sos.moreoptions.game.action;

/**
 * Used to hide an element on the screen.
 */
public interface Hideable {
    default void hide() {};

     default void hideAction(VoidAction hideAction) {
         throw new UnsupportedOperationException("Method is not implemented");
     }
}
