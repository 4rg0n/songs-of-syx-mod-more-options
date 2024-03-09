package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.BiAction;

/**
 * Called when an ui element goes through the rendering cycle
 *
 * @param <Element> type of the element which is rendered
 */
public interface Renderable<Element> {

    default void renderAction(BiAction<Element, Float> renderAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
