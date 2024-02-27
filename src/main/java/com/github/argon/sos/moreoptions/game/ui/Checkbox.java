package com.github.argon.sos.moreoptions.game.ui;

import snake2d.SPRITE_RENDERER;
import util.gui.misc.GButt;

/**
 * Adds some basic toggle functionality to {@link Checkbox}
 */
public class Checkbox extends GButt.Checkbox implements Valuable<Boolean, Checkbox> {

    private UIAction<Checkbox> clickAction = o -> {};
    private UIAction<Checkbox> renderObjectAction = o -> {};

    public Checkbox() {
    }

    public Checkbox(boolean selected) {
        selectedSet(selected);
    }

    @Override
    protected void clickA() {
        super.clickA();
        selectedToggle();
        clickAction.accept(this);
    }

    @Override
    protected void renAction() {
        super.renAction();
        renderObjectAction.accept(this);
    }

    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(r, ds, isActive, isSelected, isHovered);
    }

    public void onClick(UIAction<Checkbox> action) {
        clickAction = action;
    }

    public void onRender(UIAction<Checkbox> action) {
        renderObjectAction = action;
    }

    @Override
    public Boolean getValue() {
        return selectedIs();
    }

    @Override
    public void setValue(Boolean value) {
        selectedSet(value);
    }
}
