package com.github.argon.sos.mod.sdk.game.ui;

import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * For replacing ui elements in an area
 */
public class ViewSwitcher extends AbstractUISwitcher {

    private RENDEROBJ current;

    public ViewSwitcher(RENDEROBJ current, boolean center) {
        super(current, center);
        this.current = current;
    }

    public ViewSwitcher(int width, int height, boolean center) {
        super(width,height, center);
    }

    public void set(RENDEROBJ current) {
        this.current = current;
    }

    @Override
    protected RENDEROBJ pget() {
        return current;
    }

    public RENDEROBJ current() {
        return current;
    }
}
