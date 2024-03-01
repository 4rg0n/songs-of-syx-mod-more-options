package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.BiAction;

public interface Renderable<Element> {

    default void onRender(BiAction<Element, Float> renderAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
