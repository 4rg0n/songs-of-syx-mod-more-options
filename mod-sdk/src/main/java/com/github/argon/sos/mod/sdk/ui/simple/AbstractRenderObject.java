package com.github.argon.sos.mod.sdk.ui.simple;

import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * Base class for render objects.
 */
public abstract class AbstractRenderObject extends RENDEROBJ.RenderImp {

    /**
     * Creates a new render object with given width and height.
     *
     * @param width of the render object
     * @param height of the render object
     */
    public AbstractRenderObject(int width, int height) {
        super(width, height);
    }

    /**
     * Creates a new render object without any width and height.
     */
    public AbstractRenderObject() {
    }

    /**
     * Creates a new render object without given size as width and height.
     *
     * @param size for width and height
     */
    public AbstractRenderObject(int size) {
        super(size);
    }

    /**
     * Returns the {@link Rec} body.
     *
     * @return body
     */
    @Override
    public Rec body() {
        return body;
    }

    /**
     * Executed when the object is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since the last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {

    }
}
