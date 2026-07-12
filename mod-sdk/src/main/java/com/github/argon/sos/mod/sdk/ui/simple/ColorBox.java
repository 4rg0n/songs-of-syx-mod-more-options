package com.github.argon.sos.mod.sdk.ui.simple;

import com.github.argon.sos.mod.sdk.ui.util.Section;
import lombok.Getter;
import lombok.Setter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * Simple box, which can have a colored background.
 */
@Setter
@Getter
public class ColorBox extends Section {

    private COLOR color = COLOR.PURPLE;

    /**
     * Creates a new {@link ColorBox} with {@link COLOR#PURPLE} as background.
     */
    public ColorBox() {
    }

    /**
     * Creates a new {@link ColorBox} with given {@link COLOR} as background.
     *
     * @param color to use as background
     */
    public ColorBox(COLOR color) {
        this.color = color;
    }

    /**
     * Creates a new {@link ColorBox} with given size for width and height and {@link COLOR} as background.
     *
     * @param size for width and height
     * @param color to use as background
     */
    public ColorBox(int size, COLOR color) {
        this(size, size, color);
    }

    /**
     * Creates a new {@link ColorBox} with given width, height and {@link COLOR} as background.
     *
     * @param width of the box
     * @param height of the box
     * @param color to use as background
     */
    public ColorBox(int width, int height, COLOR color) {
        body().setWidth(width);
        body().setHeight(height);
        this.color = color;
    }

    /**
     * Executed when the color box is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        color.render(renderer, body());
        super.render(renderer, deltaSeconds);
    }

    /**
     * Creates a new {@link ColorBox} with given size, {@link COLOR} and ui element in it.
     *
     * @param uiElement to add to the color box
     * @param size of the box
     * @param color of the background
     * @return created color box
     */
    public static ColorBox of(RENDEROBJ uiElement, int size, COLOR color) {
        ColorBox colorBox = new ColorBox(size, color);
        colorBox.add(uiElement);

        return colorBox;
    }

    /**
     * Creates a new {@link ColorBox} with given {@link COLOR} and ui element in it.
     *
     * @param uiElement to add to the color box
     * @param color of the background
     * @return created color box
     */
    public static ColorBox of(RENDEROBJ uiElement, COLOR color) {
        ColorBox colorBox = new ColorBox(color);
        colorBox.add(uiElement);

        return colorBox;
    }
}
