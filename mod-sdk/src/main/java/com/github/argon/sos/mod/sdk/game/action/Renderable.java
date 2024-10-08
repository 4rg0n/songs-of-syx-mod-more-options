package com.github.argon.sos.mod.sdk.game.action;

/**
 * Called when an ui element goes through the rendering cycle
 */
public interface Renderable {

    default void renderAction(Action<Float> renderAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
