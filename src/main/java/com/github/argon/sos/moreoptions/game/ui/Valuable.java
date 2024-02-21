package com.github.argon.sos.moreoptions.game.ui;

import snake2d.util.gui.renderable.RENDEROBJ;

public interface Valuable<T> extends RENDEROBJ {
    T getValue();

    void setValue(T value);
}
