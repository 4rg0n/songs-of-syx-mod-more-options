package com.github.argon.sos.moreoptions.game.ui;

public interface Showable<T> {
    default void show() {};

     void onShow(Action<T> showAction);
}
