package com.github.argon.sos.moreoptions.game.ui;

public interface Refreshable<T> {


    default void refresh() {};

    void onRefresh(Action<T> refreshAction);
}
