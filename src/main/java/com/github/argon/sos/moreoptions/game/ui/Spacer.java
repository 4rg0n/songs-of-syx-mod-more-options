package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * For adding space between other UI elements
 */
public class Spacer extends RENDEROBJ.RenderImp {

    public Spacer(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {

    }
}
