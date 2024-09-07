package com.github.argon.sos.mod.sdk.game.ui;

import com.github.argon.sos.mod.sdk.game.ui.Section;
import lombok.Getter;
import lombok.Setter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * Simple box which can have a colored background
 */
public class ColorBox extends Section {

    @Setter
    @Getter
    private COLOR color = COLOR.PURPLE;

    public ColorBox() {
    }

    public ColorBox(COLOR color) {
        this.color = color;
    }


    public ColorBox(int size, COLOR color) {
        this(size, size, color);
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

    public static ColorBox of(RENDEROBJ render, COLOR color) {
        ColorBox colorBox = new ColorBox(color);
        colorBox.add(render);

        return colorBox;
    }

    public static ColorBox of(RENDEROBJ render, int size, COLOR color) {
        ColorBox colorBox = new ColorBox(color);
        colorBox.addCenter(render);

        return colorBox;
    }
}
