package com.github.argon.sos.mod.sdk.ui.simple;

import com.github.argon.sos.mod.sdk.ui.util.Section;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;

/**
 * Simple circle which can have a colored background.
 */
public class ColorCircle extends Section {

    private final Sprite circle;

    /**
     * Creates a new {@link ColorCircle} with the given radius and {@link COLOR}.
     *
     * @param radius of the circle
     * @param color of the circle
     */
    public ColorCircle(int radius, COLOR color) {
        circle = new Sprite(radius, color);
        body().setDim(radius * 2);
    }

    /**
     * Returns the {@link COLOR}.
     *
     * @return color
     */
    public COLOR getColor() {
        return circle.getColor();
    }

    /**
     * Sets the color {@link COLOR}.
     *
     * @param color to set
     */
    public void setColor(COLOR color) {
        circle.setColor(color);
    }

    /**
     * Executed when the circle is rendered
     *
     * @param renderer for rendering
     * @param deltaSeconds since last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        circle.render(renderer, body().x1(), body().x2(), body().y1(), body().y2());
        super.render(renderer, deltaSeconds);
    }

    /**
     * The actual circular sprite, which is drawn.
     */
    public static class Sprite extends SPRITE.Imp {

        @Setter
        @Getter
        private COLOR color = COLOR.PURPLE;

        private final int radius;

        /**
         * Creates a new circle {@link Sprite} with the given radius and {@link COLOR}.
         *
         * @param radius of the circle
         * @param color of the circle
         */
        public Sprite(int radius, COLOR color) {
            this.radius = radius;
            this.color = color;
        }

        /**
         * Draws the actual circle.
         *
         * @param renderer to use
         * @param X1 left-hand position
         * @param X2 right-hand position
         * @param Y1 upper position
         * @param Y2 bottom position
         */
        @Override
        public void render(SPRITE_RENDERER renderer, int X1, int X2, int Y1, int Y2) {
            int centerY = Y1 + (Y2 - Y1) / 2;
            color.bind();
            UI.icons().s.dot.renderCY(renderer,X1 + radius, centerY);
            COLOR.unbind();
        }
    }
}
