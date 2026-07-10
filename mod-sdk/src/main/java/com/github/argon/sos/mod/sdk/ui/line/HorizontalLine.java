package com.github.argon.sos.mod.sdk.ui.line;

import com.github.argon.sos.mod.sdk.ui.simple.AbstractRenderObject;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import util.colors.GCOLOR;

/**
 * Draws a horizontal pixel line.
 */
public class HorizontalLine extends AbstractRenderObject {

    private final int thickness;
    private final boolean shadow;

    /**
     * Creates a new {@link HorizontalLine} with given thickness.
     *
     * @param thickness of the line
     */
    public HorizontalLine(int thickness) {
        this(thickness, false);
    }

    /**
     * Creates a new {@link HorizontalLine} with given thickness.
     *
     * @param thickness of the line
     * @param shadow whether the line shall have a shadow
     */
    public HorizontalLine(int thickness, boolean shadow) {
        this.thickness = thickness;
        this.shadow = shadow;
    }

    /**
     * Creates a new {@link HorizontalLine} with given thickness.
     *
     * @param width of the view containing the line
     * @param height of the view containing the line
     * @param thickness of the line
     */
    public HorizontalLine(int width, int height, int thickness) {
        this(width, height, thickness, false);
    }

    /**
     * Creates a new {@link HorizontalLine} with given thickness.
     *
     * @param width of the view containing the line
     * @param height of the view containing the line
     * @param thickness of the line
     * @param shadow whether the line shall have a shadow
     */
    public HorizontalLine(int width, int height, int thickness, boolean shadow) {
        super(width, height);
        this.thickness = thickness;
        this.shadow = shadow;
    }

    /**
     * Executed when the line is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since the last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        super.render(renderer, deltaSeconds);
        GCOLOR.UI().border().render(renderer, body().x1(), body().x2(), body().cY() + thickness, body().cY() + 1 - thickness);

        if (shadow) {
            COLOR.WHITE10.render(renderer, body().x1(), body().x2(), body().cY() + thickness + thickness, body().cY() + 1 - thickness + thickness);
        }
    }
}
