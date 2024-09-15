package com.github.argon.sos.mod.sdk.game.action;

public interface Switchable<Key> {
    default void switch_(Key key) {}

    default void switchAction(Action<Key> toggleAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
