package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GButt;

/**
 * Adds some basic toggle functionality to {@link GButt.CheckboxTitle}
 */
public class CheckboxTitle extends GButt.CheckboxTitle {
    public CheckboxTitle(SPRITE label) {
        super(label);
    }

    public CheckboxTitle(CharSequence text) {
        super(text);
    }

    protected void clickA() {
        selectedToggle();
    }

    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(r, ds, isActive, isSelected, isHovered);
    }
}
