package com.github.argon.sos.mod.sdk.ui.simple;

/**
 * An empty transparent ui element for adding space between other UI elements.
 */
public class Spacer extends AbstractRenderObject {

    /**
     * Creates a new {@link Spacer} with 0 width and height.
     */
    public Spacer() {
        super();
    }

    /**
     * Creates a new {@link Spacer} with given size as width and height.
     *
     * @param size for width and height
     */
    public Spacer(int size) {
        super(size, size);
    }

    /**
     * Creates a new {@link Spacer} with given width and height.
     *
     * @param width of the spacer
     * @param height of the spacer
     */
    public Spacer(int width, int height) {
        super(width, height);
    }
}
