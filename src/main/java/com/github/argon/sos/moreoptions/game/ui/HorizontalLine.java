package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import util.colors.GCOLOR;

/**
 * Draws a horizontal pixel line
 */
public class HorizontalLine extends AbstractRender {

    private final int thickness;
    private final boolean shadow;

    public HorizontalLine(int thickness) {
        this(thickness, false);
    }

    public HorizontalLine(int thickness, boolean shadow) {
        this.thickness = thickness;
        this.shadow = shadow;
    }

    public HorizontalLine(int width, int height, int thickness) {
        this(width, height, thickness, false);
    }

    public HorizontalLine(int width, int height, int thickness, boolean shadow) {
        super(width, height);
        this.thickness = thickness;
        this.shadow = shadow;
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {
        super.render(spriteRenderer, v);
        GCOLOR.UI().border().render(spriteRenderer, body().x1(), body().x2(), body().cY() + thickness, body().cY() + 1 - thickness);

        if (shadow) {
            COLOR.WHITE10.render(spriteRenderer, body().x1(), body().x2(), body().cY() + thickness + thickness, body().cY() + 1 - thickness + thickness);
        }
    }
}
