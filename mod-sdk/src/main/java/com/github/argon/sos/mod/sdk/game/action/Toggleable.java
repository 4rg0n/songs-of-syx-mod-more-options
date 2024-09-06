package com.github.argon.sos.mod.sdk.game.action;

public interface Toggleable<Value> {
    default void toggle() {}

    default void toggleAction(Action<Value> toggleAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
