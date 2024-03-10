package com.github.argon.sos.moreoptions.game.ui;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.misc.ACTION;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GButt;

public class Button extends GButt.ButtPanel {

    private COLOR color = COLOR.WHITE35;

    private boolean markSuccess = false;
    private boolean markError = false;

    private double markUpdateTimerSeconds = 0d;
    private final static int MARK_DURATION_SECONDS = 1;

    @Setter
    @Accessors(fluent = true)
    private boolean clickable = true;

    @Setter
    @Accessors(fluent = true)
    private boolean hoverable = true;
    @Nullable
    private ACTION renAction;

    public Button(CharSequence label) {
        this(label, COLOR.WHITE35, null);
    }

    public Button(CharSequence label, CharSequence description) {
        this(label, COLOR.WHITE35, description);
    }

    public Button(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label);
        this.color = color;
        bg(color);
        hoverInfoSet(description);
    }

    public Button(SPRITE label) {
        super(label);
    }

    public Button(SPRITE label, COLOR color) {
        super(label);
        this.color = color;
        bg(color);
    }

    public Button markApplied(boolean applied) {
        if (applied) {
            bgClear();
        } else {
            bg(COLOR.WHITE15WHITE50);
        }

        return this;
    }

    @Override
    public boolean click() {
        if (!clickable) return false;
        return super.click();
    }

    @Override
    public boolean hover(COORDINATE mCoo) {
        if (!hoverable) return false;
        return super.hover(mCoo);
    }

    @Override
    protected void render(SPRITE_RENDERER r, float seconds, boolean isActive, boolean isSelected, boolean isHovered) {
        if (!activeIs()) {
            super.render(r, seconds, false, false, false);
        } else {
            super.render(r, seconds, isActive, isSelected, isHovered);
        }

        // clear error or success mark after duration
        if (markError || markSuccess) {
            if (markUpdateTimerSeconds >= MARK_DURATION_SECONDS) {
                markUpdateTimerSeconds = 0d;
                markSuccess = false;
                markError = false;
                bgClear();
            } else {
                markUpdateTimerSeconds += seconds;
            }
        }
    }

    /**
     * Let the button blink red or green for ~1 second
     */
    public Button markSuccess(boolean success) {
        if (success) {
            bg(COLOR.GREEN40);
            markSuccess = true;
        } else {
            bg(COLOR.RED50);
            markError = true;
        }

        return this;
    }

    @Override
    public Button bgClear() {
        super.bg(color);
        return this;
    }

    @Override
    public Button bg(COLOR c) {
        super.bg(c);
        return this;
    }

    @Override
    protected void renAction() {
        if (renAction != null) renAction.exe();
        super.renAction();
    }

    public CLICKABLE renActionSet(ACTION action) {
        this.renAction = action;
        return this;
    }
}
