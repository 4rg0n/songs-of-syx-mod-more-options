package com.github.argon.sos.moreoptions.game.ui;

import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GButt;

public class Button extends GButt.ButtPanel {

    private COLOR color = COLOR.WHITE35;

    private boolean markSuccess = false;
    private boolean markError = false;

    @Getter
    private boolean enabled = true;

    private double markUpdateTimerSeconds = 0d;

    private final static int MARK_DURATION_SECONDS = 1;


    public Button(CharSequence label) {
        this(label, COLOR.WHITE35);
    }

    public Button(CharSequence label, COLOR color) {
        super(label);
        this.color = color;
        bg(color);
    }

    public void setEnabled(boolean enabled) {
        activeSet(enabled);
        this.enabled = enabled;
    }

    public Button(SPRITE label) {
        super(label);
    }

    public Button(SPRITE label, COLOR color) {
        super(label);
        this.color = color;
        bg(color);
    }

    public ButtPanel markApplied(boolean applied) {
        if (applied) {
            bgClear();
        } else {
            bg(COLOR.WHITE15WHITE50);
        }

        return this;
    }

    @Override
    protected void render(SPRITE_RENDERER r, float seconds, boolean isActive, boolean isSelected, boolean isHovered) {
        if (!isEnabled()) {
            super.render(r, seconds, false, false, false);
        } else {
            super.render(r, seconds, isActive, isSelected, isHovered);
        }

        // clear error or success mark after duration
        if (markError || markSuccess) {
            if (markUpdateTimerSeconds >= MARK_DURATION_SECONDS) {
                markUpdateTimerSeconds = 0d;
                bgClear();
            }

            markUpdateTimerSeconds += seconds;
        }
    }

    /**
     * Let the button blink red or green for ~1 second
     */
    public ButtPanel markSuccess(boolean success) {
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
    public ButtPanel bgClear() {
        bg(color);
        return this;
    }
}
