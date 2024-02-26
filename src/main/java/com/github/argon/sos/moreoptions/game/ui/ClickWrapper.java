package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.SoundEffect;
import snake2d.util.datatypes.*;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.Hoverable.HOVERABLE;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;

public abstract class ClickWrapper implements CLICKABLE {

    public boolean dirty = false;
    private final Rec body = new Rec() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        @Override
        public Rec moveX1(double X1) {

            dirty = true;
            return super.moveX1(X1);
        };
        @Override
        public Rec moveY1(double X1) {

            dirty = true;
            return super.moveY1(X1);
        };
    };

    private final boolean center;

    private DIR dd = DIR.NW;

    public ClickWrapper(DIMENSION dim, boolean center) {
        body.setDim(dim);
        this.center = center;
    }

    public ClickWrapper(int width, int height, boolean center) {
        body.setDim(width, height);
        this.center = center;
    }

    public ClickWrapper(RENDEROBJ obj, boolean center) {
        body.set(obj.body());
        this.center = center;
    }

    public ClickWrapper setD(DIR dd) {
        this.dd = dd;
        return this;
    }

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
            o.body().incrX(dw*dd.x());
            o.body().incrY(dh*dd.y());
        }

        this.body.unify(o.body());
        return o;
    }
    protected abstract RENDEROBJ pget();

    private HOVERABLE hov() {
        if (get() != null && get() instanceof HOVERABLE)
            return (HOVERABLE) get();
        return null;
    }

    private CLICKABLE cli() {
        if (get() != null && get() instanceof CLICKABLE)
            return (CLICKABLE) get();
        return null;
    }

    @Override
    public boolean activeIs() {
        if (cli() == null)
            return false;
        return cli().activeIs();
    }

    @Override
    public boolean hover(COORDINATE mCoo) {
        dirty = true;
        if (hov() == null)
            return false;
        return hov().hover(mCoo);
    }

    @Override
    public boolean hoveredIs() {
        if (hov() == null)
            return false;
        return hov().hoveredIs();
    }

    @Override
    public void hoverInfoGet(GUI_BOX text) {
        if (hov() != null)
            hov().hoverInfoGet(text);
    }

    @Override
    public boolean click() {
        if (cli() == null)
            return false;
        return cli().click();
    }

    @Override
    public boolean visableIs() {
        if (get() == null)
            return false;
        return get().visableIs();
    }

    @Override
    public RECTANGLEE body() {
        return body;
    }

    @Override
    public final void render(SPRITE_RENDERER r, float ds) {
        dirty = true;
        if (get() != null) {
            get().render(r, ds);
        }
        renAction();
        dirty = true;
    }

    protected void renAction() {

    }

    @Override
    public CLICKABLE hoverSoundSet(SoundEffect sound) {
        if (cli() == null)
            return null;
        return cli().hoverSoundSet(sound);
    }

    @Override
    public CLICKABLE hoverInfoSet(CharSequence s) {
        if (hov() == null)
            return null;
        hov().hoverInfoSet(s);
        return cli();
    }

    @Override
    public CLICKABLE hoverTitleSet(CharSequence s) {
        if (hov() == null)
            return null;
        hov().hoverTitleSet(s);
        return cli();
    }

    @Override
    public CLICKABLE clickSoundSet(SoundEffect sound) {
        if (cli() == null)
            return null;
        return cli().clickSoundSet(sound);
    }

    @Override
    public CLICKABLE activeSet(boolean activate) {
        if (cli() == null)
            return null;
        return cli().activeSet(activate);
    }

    @Override
    public CLICKABLE selectedSet(boolean yes) {
        if (cli() == null)
            return null;
        return cli().selectedSet(yes);
    }

    @Override
    public CLICKABLE selectTmp() {
        if (cli() == null)
            return null;
        return cli().selectTmp();
    }

    @Override
    public CLICKABLE selectedToggle() {
        if (cli() == null)
            return null;
        return cli().selectedToggle();
    }

    @Override
    public CLICKABLE visableSet(boolean yes) {
        if (cli() == null)
            return null;
        return cli().visableSet(yes);
    }

    @Override
    public boolean selectedIs() {
        if (cli() == null)
            return false;
        return cli().selectedIs();
    }

    @Override
    public CLICKABLE clickActionSet(ACTION f) {
        if (cli() == null)
            return null;
        return cli().clickActionSet(f);
    }

}
