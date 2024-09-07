package com.github.argon.sos.mod.sdk.game.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Toggleable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.SPRITE_RENDERER;
import util.gui.misc.GButt;

/**
 * Adds some basic toggle functionality to {@link GButt.Checkbox}
 */
public class Checkbox extends GButt.Checkbox implements Valuable<Boolean>, Toggleable<Boolean> {

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction clickAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction renderObjectAction = () -> {};
    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Boolean> toggleAction = o -> {};

    public Checkbox(boolean selected) {
        selectedSet(selected);
    }

    @Override
    public void toggle() {
        selectedToggle();
        toggleAction.accept(selectedIs());
    }

    @Override
    protected void clickA() {
        super.clickA();
        toggle();
        clickAction.accept();
    }

    @Override
    protected void renAction() {
        super.renAction();
        renderObjectAction.accept();
    }

    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(r, ds, isActive, isSelected, isHovered);
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
