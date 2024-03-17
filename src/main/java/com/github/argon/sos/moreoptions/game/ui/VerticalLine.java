package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import util.colors.GCOLOR;

/**
 * Draws a vertical pixel line
 */
public class VerticalLine extends AbstractRender {

    private final int thickness;

    public VerticalLine(int thickness) {
        this.thickness = thickness;
    }

    public VerticalLine(int width, int height, int thickness) {
        super(width, height);
        this.thickness = thickness;
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {
        GCOLOR.UI().border().render(spriteRenderer, body().cX() + thickness, body().cX() + 1 - thickness, body().y1() , body().y2());
    }
}
