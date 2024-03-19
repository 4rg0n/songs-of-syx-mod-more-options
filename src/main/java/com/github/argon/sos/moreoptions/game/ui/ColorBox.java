package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

public class ColorBox extends GuiSection {

    private final COLOR color;

    public ColorBox(COLOR color) {
        this.color = color;
    }

    public ColorBox(int width, int height, COLOR color) {
        body().setWidth(width);
        body().setHeight(height);
        this.color = color;
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        color.render(r, body());
        super.render(r, ds);
    }
}
