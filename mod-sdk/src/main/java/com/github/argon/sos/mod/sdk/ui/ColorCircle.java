package com.github.argon.sos.mod.sdk.ui;

import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;

/**
 * Simple box which can have a colored background
 */
public class ColorCircle extends Section {

    private final Sprite circle;

    public ColorCircle(int radius, COLOR color) {
        circle = new Sprite(radius, color);
        body().setDim(radius * 2);
    }

    public COLOR getColor() {
        return circle.getColor();
    }

    public void setColor(COLOR color) {
        circle.setColor(color);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        circle.render(r, body().x1(), body().x2(), body().y1(), body().y2());
        super.render(r, ds);
    }

    public static class Sprite extends SPRITE.Imp {

        @Setter
        @Getter
        private COLOR color = COLOR.PURPLE;

        private int radius;

        public Sprite() {
        }

        public Sprite(int radius, COLOR color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void render(SPRITE_RENDERER r, int X1, int X2, int Y1, int Y2) {
            int CY = Y1 + (Y2 - Y1) / 2;
            color.bind();
            UI.icons().s.dot.renderCY(r,X1 + radius, CY);
            COLOR.unbind();
        }
    }
}
