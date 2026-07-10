package com.github.argon.sos.mod.sdk.ui.switcher;

import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * For replacing ui elements in an area
 */
public class ViewSwitcher extends AbstractUiSwitcher {

    private RENDEROBJ current;

    /**
     * Creates a new {@link ViewSwitcher}.
     *
     * @param current view to display
     * @param center whether content should center vertically and horizontally
     */
    public ViewSwitcher(RENDEROBJ current, boolean center) {
        super(current, center);
        this.current = current;
    }

    /**
     * Creates a new {@link ViewSwitcher}.
     *
     * @param width of the view
     * @param height of the view
     * @param center whether content should center vertically and horizontally
     */
    public ViewSwitcher(int width, int height, boolean center) {
        super(width,height, center);
    }

    /**
     * Will switch the current displayed view to the given one.
     *
     * @param newView to switch to
     */
    public void set(RENDEROBJ newView) {
        this.current = newView;
    }

    /**
     * @see ViewSwitcher#current()
     */
    @Override
    protected RENDEROBJ pget() {
        return current();
    }

    /**
     * Returns the currently displayed view.
     *
     * @return currently displayed view
     */
    public RENDEROBJ current() {
        return current;
    }
}
