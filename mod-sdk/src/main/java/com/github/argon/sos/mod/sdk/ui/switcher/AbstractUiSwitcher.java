package com.github.argon.sos.mod.sdk.ui.switcher;

import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.SoundEffect;
import snake2d.util.datatypes.*;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.Hoverable.HOVERABLE;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;

import java.io.Serial;

/**
 * Base class for switching currently shown content with different content.
 */
public abstract class AbstractUiSwitcher implements CLICKABLE {

    public boolean dirty = false;
    private final Rec body = new Rec() {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Rec moveX1(double X1) {
            dirty = true;
            return super.moveX1(X1);
        }

        @Override
        public Rec moveY1(double Y1) {
            dirty = true;
            return super.moveY1(Y1);
        }
    };

    private final boolean center;

    private DIR direction = DIR.NW;

    /**
     * Creates a new ui switcher.
     *
     * @param dimension with width and height for the view
     * @param center whether content should center vertically and horizontally
     */
    public AbstractUiSwitcher(DIMENSION dimension, boolean center) {
        body.setDim(dimension);
        this.center = center;
    }

    /**
     * Creates a new ui switcher.
     *
     * @param width of the view
     * @param height of the view
     * @param center whether content should center vertically and horizontally
     */
    public AbstractUiSwitcher(int width, int height, boolean center) {
        body.setDim(width, height);
        this.center = center;
    }

    /**
     * Creates a new ui switcher.
     *
     * @param renderObject used for size of the view only
     * @param center whether content should center vertically and horizontally
     */
    public AbstractUiSwitcher(RENDEROBJ renderObject, boolean center) {
        body.set(renderObject.body());
        this.center = center;
    }

    /**
     * Where the view shall be displayed.
     *
     * @param direction to use
     * @return this
     */
    public AbstractUiSwitcher setDirection(DIR direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Returns the view as {@link RENDEROBJ}.
     *
     * @return view as {@link RENDEROBJ} or null when there is none
     */
    @Nullable
    private RENDEROBJ get() {
        RENDEROBJ o = pget();
        if (o == null)
            return null;
        if (dirty && center) {
            dirty = false;
            o.body().moveC(body.cX(), body.cY());
        } else if (dirty) {
            dirty = false;
            int dw = (body.width() - o.body().width())/2;
            int dh = (body.height() - o.body().height())/2;
            o.body().moveC(body.cX(), body.cY());
            o.body().incrX(dw* direction.x());
            o.body().incrY(dh* direction.y());
        }

        this.body.unify(o.body());
        return o;
    }
    protected abstract RENDEROBJ pget();

    /**
     * Returns the view as {@link HOVERABLE}.
     *
     * @return view as {@link HOVERABLE} or null if it isn't a {@link HOVERABLE}
     */
    @Nullable
    private HOVERABLE hov() {
        RENDEROBJ renderobj = get();
        if (renderobj instanceof HOVERABLE) return (HOVERABLE) renderobj;

        return null;
    }

    /**
     * Returns the view as {@link CLICKABLE}.
     *
     * @return view as {@link CLICKABLE} or null if it isn't a {@link CLICKABLE}
     */
    @Nullable
    private CLICKABLE cli() {
        RENDEROBJ renderobj = get();
        if (renderobj instanceof CLICKABLE) return (CLICKABLE) renderobj;

        return null;
    }

    /**
     * Whether the view is active.
     *
     * @return whether the view is active
     */
    @Override
    public boolean activeIs() {
        CLICKABLE cli = cli();
        if (cli == null) return false;

        return cli.activeIs();
    }

    /**
     * Executed when the view is hovered with the mouse.
     *
     * @param mouseCoordinates coordinates of the mouse pointer
     * @return whether the view is hover-able
     */
    @Override
    public boolean hover(COORDINATE mouseCoordinates) {
        dirty = true;
        HOVERABLE hov = hov();
        if (hov == null) return false;

        return hov.hover(mouseCoordinates);
    }

    /**
     * Whether the view is hovered.
     *
     * @return whether the view is hovered
     */
    @Override
    public boolean hoveredIs() {
        HOVERABLE hov = hov();
        if (hov == null) return false;

        return hov.hoveredIs();
    }

    /**
     * Fills the given {@link GUI_BOX} with hover information.
     *
     * @param text to fill information into
     */
    @Override
    public void hoverInfoGet(GUI_BOX text) {
        HOVERABLE hov = hov();
        if (hov != null) hov.hoverInfoGet(text);
    }


    @Override
    public boolean click() {
        CLICKABLE cli = cli();
        if (cli == null) return false;

        return cli.click();
    }

    @Override
    public boolean visableIs() {
        RENDEROBJ renderobj = get();
        if (renderobj == null) return false;

        return renderobj.visableIs();
    }

    /**
     * Executed when the view is clicked
     *
     * @return whether the button is clickable
     */
    @Override
    public RECTANGLEE body() {
        return body;
    }

    /**
     * Executed when the ui switcher is rendered.
     * Executes {@link AbstractUiSwitcher#renAction()}
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     */
    @Override
    public final void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        dirty = true;
        RENDEROBJ renderobj = get();
        if (renderobj != null) renderobj.render(renderer, deltaSeconds);

        renAction();
        dirty = true;
    }

    /**
     * Optional render action.
     */
    protected void renAction() {

    }

    /**
     * Sets a sound to be played when hovered.
     *
     * @param sound to play
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE hoverSoundSet(SoundEffect sound) {
        CLICKABLE cli = cli();
        if (cli == null) return null;
        return cli.hoverSoundSet(sound);
    }

    /**
     * Sets a text, which is displayed when hovering the view.
     *
     * @param text to set
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE hoverInfoSet(CharSequence text) {
        HOVERABLE hov = hov();
        if (hov == null) return null;

        hov.hoverInfoSet(text);
        return cli();
    }

    /**
     * Sets a text, which is displayed when the title of the view is hovered.
     *
     * @param text to set
     * @return view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE hoverTitleSet(CharSequence text) {
        HOVERABLE hov = hov();
        if (hov == null) return null;

        hov.hoverTitleSet(text);
        return cli();
    }

    /**
     * Sets a sound to be played when clicked.
     *
     * @param sound to play
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE clickSoundSet(SoundEffect sound) {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.clickSoundSet(sound);
    }

    /**
     * Tries to set the views active state.
     *
     * @param activate whether it is active or not
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE activeSet(boolean activate) {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.activeSet(activate);
    }

    /**
     * Tries to set the views select state.
     *
     * @param selected whether it is selected or not
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE selectedSet(boolean selected) {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.selectedSet(selected);
    }

    /**
     * Tries to select the view temporarily.
     *
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE selectTmp() {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.selectTmp();
    }

    /**
     * Tries to toggle the views select state.
     *
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE selectedToggle() {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.selectedToggle();
    }

    /**
     * Tries to set the views visibility state.
     *
     * @param visible whether it is visible or not
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE visableSet(boolean visible) {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.visableSet(visible);
    }

    /**
     * Tries to fetch the selected state of the view
     *
     * @return whether view is selected or not
     */
    @Override
    public boolean selectedIs() {
        CLICKABLE cli = cli();
        if (cli == null) return false;

        return cli.selectedIs();
    }

    /**
     * Sets an optional action, which will be executed when the view is clicked.
     *
     * @param action to set
     * @return the view as {@link CLICKABLE}
     */
    @Nullable
    @Override
    public CLICKABLE clickActionSet(ACTION action) {
        CLICKABLE cli = cli();
        if (cli == null) return null;

        return cli.clickActionSet(action);
    }
}
