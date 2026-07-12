package com.github.argon.sos.mod.sdk.game.action;

/**
 * Called when an ui element goes through the rendering cycle
 */
public interface Renderable {

    /**
     * Optional action to be executed when an ui element is rendered.
     *
     * @param renderAction to be executed when rendering
     */
    default void renderAction(Action<Float> renderAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
