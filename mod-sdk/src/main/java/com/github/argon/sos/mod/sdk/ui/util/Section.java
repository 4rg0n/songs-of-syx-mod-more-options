package com.github.argon.sos.mod.sdk.ui.util;

import com.github.argon.sos.mod.sdk.game.action.*;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * A section is something like a <pre><div></pre> container in HTML.
 * It holds and aligns multiple other ui elements in it.
 * Adds some common used functionality to the games default {@link GuiSection}.
 */
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

    /**
     * Hides the section and all what is in it.
     */
    @Override
    public void hide() {
        visableSet(false);
    }

    /**
     * Shows the section and all what is in it.
     */
    @Override
    public void show() {
        visableSet(true);
    }

    /**
     * Either hides or shows the section and everything in it.
     * Will execute {@link Section#showAction} or {@link Section#hideAction}
     *
     * @param visibility whether to hide or show.
     * @return this
     */
    @Override
    public GuiSection visableSet(boolean visibility) {
        if (visibility && !visableIs()) {
            showAction.accept();
        } else if (!visibility && visableIs()) {
            hideAction.accept();
        }

        return super.visableSet(visibility);
    }

    /**
     * Executed when the section is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        renderAction.accept(deltaSeconds);
        super.render(renderer, deltaSeconds);
    }

    /**
     * Adds an ui element horizontally centered.
     *
     * @param element to add
     * @return this
     */
    public Section addCenter(RENDEROBJ element) {
        addCentredX(element, body().cX());
        return this;
    }
}
