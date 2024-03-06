package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import snake2d.SPRITE_RENDERER;
import util.gui.misc.GButt;

/**
 * Adds some basic toggle functionality to {@link Checkbox}
 */
public class Checkbox extends GButt.Checkbox implements Valuable<Boolean, Checkbox> {

    private Action<Checkbox> clickAction = o -> {};
    private Action<Checkbox> renderObjectAction = o -> {};

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

    public void onClick(Action<Checkbox> action) {
        clickAction = action;
    }

    public void onRender(Action<Checkbox> action) {
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
