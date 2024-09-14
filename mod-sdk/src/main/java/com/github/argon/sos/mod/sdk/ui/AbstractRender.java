package com.github.argon.sos.mod.sdk.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.renderable.RENDEROBJ;

public abstract class AbstractRender extends RENDEROBJ.RenderImp {

    public AbstractRender(int width, int height) {
        super(width, height);
    }

    public AbstractRender() {
    }

    public AbstractRender(int size) {
        super(size);
    }

    @Override
    public Rec body() {
        return body;
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {

    }
}
