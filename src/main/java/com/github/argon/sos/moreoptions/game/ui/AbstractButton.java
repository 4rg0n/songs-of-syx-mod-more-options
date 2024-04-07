package com.github.argon.sos.moreoptions.game.ui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.misc.ACTION;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GButt;

public abstract class AbstractButton<Value, Element extends GButt.ButtPanel> extends GButt.ButtPanel implements Valuable<Value, Element>, Searchable<Value, Boolean> {
    @Getter
    protected COLOR color = COLOR.WHITE35;

    private boolean markSuccess = false;
    private boolean markError = false;

    private double markUpdateTimerSeconds = 0d;
    private final static int MARK_DURATION_SECONDS = 1;

    @Getter
    @Setter
    protected Value value;

    @Setter
    @Accessors(fluent = true)
    protected boolean clickable = true;

    @Setter
    @Accessors(fluent = true)
    protected boolean hoverable = true;
    @Nullable
    protected ACTION renAction;

    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    protected Value searchTerm;

    public AbstractButton(CharSequence label) {
        this(label, COLOR.WHITE35, null);
    }

    public AbstractButton(CharSequence label, CharSequence description) {
        this(label, COLOR.WHITE35, description);
    }

    public AbstractButton(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label);
        this.color = color;
        bg(color);
        hoverInfoSet(description);
    }

    public AbstractButton(SPRITE label) {
        super(label);
    }

    public AbstractButton(SPRITE label, @Nullable CharSequence description) {
        super(label);
        hoverInfoSet(description);
    }

    public AbstractButton(SPRITE label, COLOR color) {
        super(label);
        this.color = color;
        bg(color);
    }

    public SPRITE getLabel() {
        return label;
    }

    public void markApplied(boolean applied) {
        if (applied) {
            bgClear();
        } else {
            bg(COLOR.WHITE15WHITE50);
        }
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
    public void markSuccess(boolean success) {
        if (success) {
            bg(COLOR.GREEN40);
            markSuccess = true;
        } else {
            bg(COLOR.RED50);
            markError = true;
        }
    }

    @Override
    public Element bgClear() {
        super.bgClear();
        return element();
    }

    @Override
    public Element bg(COLOR c) {
        color = c;
        super.bg(c);
        return element();
    }

    @Override
    protected void renAction() {
        if (renAction != null) renAction.exe();
        super.renAction();
    }

    public Element renActionSet(ACTION action) {
        this.renAction = action;
        return element();
    }

    protected abstract Element element();

    @Override
    public Boolean search(Value s) {
        if (searchTerm == null) {
            return true;
        }

        return searchTerm.equals(s);
    }
}
