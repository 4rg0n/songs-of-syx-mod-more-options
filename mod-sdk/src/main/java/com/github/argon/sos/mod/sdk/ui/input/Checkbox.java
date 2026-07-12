package com.github.argon.sos.mod.sdk.ui.input;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Toggleable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import com.github.argon.sos.mod.sdk.game.action.VoidAction;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.SPRITE_RENDERER;
import util.gui.misc.GButt;

/**
 * A simple checkbox element.
 * Adds some basic toggle functionality to {@link GButt.Checkbox}
 */
@Setter
@Accessors(fluent = true, chain = false)
public class Checkbox extends GButt.Checkbox implements Valuable<Boolean>, Toggleable<Boolean> {

    private VoidAction clickAction = () -> {};
    private VoidAction renderAction = () -> {};
    private Action<Boolean> toggleAction = o -> {};

    /**
     * Creates a new {@link com.github.argon.sos.mod.sdk.ui.input.Checkbox} with given checked state.
     *
     * @param checked whether the checkbox is checked or not
     */
    public Checkbox(boolean checked) {
        selectedSet(checked);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggle() {
        selectedToggle();
        toggleAction.accept(selectedIs());
    }

    /**
     * Executed when the checkbox is clicked.
     */
    @Override
    protected void clickA() {
        super.clickA();
        toggle();
        clickAction.accept();
    }

    /**
     * Executed when the checkbox is rendered.
     * Will execute the {@link com.github.argon.sos.mod.sdk.ui.input.Checkbox#renderAction}.
     */
    @Override
    protected void renAction() {
        super.renAction();
        renderAction.accept();
    }

    /**
     * Executed when the checkbox is rendered
     *
     * @param renderer to render
     * @param deltaSeconds since last render loop
     * @param isActive whether the checkbox is interactable
     * @param isSelected whether the checkbox in a selected / checked state.
     * @param isHovered whether the checkbox is currently hovered
     */
    @Override
    protected void render(SPRITE_RENDERER renderer, float deltaSeconds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(renderer, deltaSeconds, isActive, isSelected, isHovered);
    }

    /**
     * Returns whether the checkbox is checked.
     *
     * @return whether the checkbox is checked
     */
    @Override
    public Boolean getValue() {
        return selectedIs();
    }

    /**
     * Sets the checked state.
     *
     * @param checked whether the checkbox is shall be checked or not
     */
    @Override
    public void setValue(Boolean checked) {
        selectedSet(checked);
    }
}
