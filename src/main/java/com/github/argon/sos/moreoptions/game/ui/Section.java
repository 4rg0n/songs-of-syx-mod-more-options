package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.*;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

public class Section extends GuiSection implements Hideable, Showable, Renderable {
    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Float> renderAction = o -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction hideAction = () -> {};

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction showAction = () -> {};

    @Override
    public void hide() {
        visableSet(false);
    }

    @Override
    public void show() {
        visableSet(true);
    }

    @Override
    public GuiSection visableSet(boolean yes) {
        if (yes && !visableIs()) {
            showAction.accept();
        } else if (!yes && visableIs()) {
            hideAction.accept();
        }

        return super.visableSet(yes);
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        renderAction.accept(ds);
        super.render(r, ds);
    }

    public Section addCenter(RENDEROBJ render) {
        addCentredX(render, body().cX());
        return this;
    }
}
