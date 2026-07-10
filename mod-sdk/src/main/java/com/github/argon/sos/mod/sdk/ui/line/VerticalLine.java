package com.github.argon.sos.mod.sdk.ui.line;

import com.github.argon.sos.mod.sdk.ui.simple.AbstractRenderObject;
import snake2d.SPRITE_RENDERER;
import util.colors.GCOLOR;

/**
 * Draws a vertical pixel line.
 */
public class VerticalLine extends AbstractRenderObject {

    private final int thickness;

    /**
     * Creates a new {@link VerticalLine} with given thickness.
     *
     * @param thickness of the line
     */
    public VerticalLine(int thickness) {
        this.thickness = thickness;
    }

    /**
     * Creates a new {@link VerticalLine} with given thickness.
     *
     * @param width of the view containing the line
     * @param height of the view containing the line
     * @param thickness of the line
     */
    public VerticalLine(int width, int height, int thickness) {
        super(width, height);
        this.thickness = thickness;
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
        GCOLOR.UI().border().render(renderer, body().cX() + thickness, body().cX() + 1 - thickness, body().y1() , body().y2());
    }
}
