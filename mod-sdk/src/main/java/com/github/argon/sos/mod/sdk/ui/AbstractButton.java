package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Searchable;
import com.github.argon.sos.mod.sdk.game.action.Toggleable;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import init.sprite.UI.UI;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.misc.ACTION;
import snake2d.util.sprite.SPRITE;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GButt;

/**
 * Basic functionality shared between buttons.
 *
 * @param <Value> a button can hold a value
 * @param <Element> the button class type itself
 */
public abstract class AbstractButton<Value, Element extends GButt.ButtPanel> extends GButt.ButtPanel
    implements Valuable<Value>,
    Searchable<Value, Boolean>,
    Toggleable<Boolean>
{
    @Getter
    protected COLOR bgColor = COLOR.WHITE35;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Boolean> toggleAction = o -> {};

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
    
    public final static COLOR DEFAULT_COLOR = COLOR.WHITE35;

    public AbstractButton(CharSequence label, Font font) {
        this(label, DEFAULT_COLOR, font, null);
    }

    public AbstractButton(CharSequence label) {
        this(label, DEFAULT_COLOR, UI.FONT().H2, null);
    }

    public AbstractButton(CharSequence label, @Nullable CharSequence description, Font font) {
        this(label, DEFAULT_COLOR, font , description);
    }

    public AbstractButton(CharSequence label, COLOR bgColor, @Nullable CharSequence description) {
        this(label, bgColor, UI.FONT().H2, description);
    }

    public AbstractButton(CharSequence label, @Nullable CharSequence description) {
        this(label, DEFAULT_COLOR, UI.FONT().H2, description);
    }

    public AbstractButton(CharSequence label, COLOR bgColor, Font font, @Nullable CharSequence description) {
        super(font.getText(label));
        this.bgColor = bgColor;
        bg(bgColor);
        hoverInfoSet(description);
    }

    public AbstractButton(SPRITE label) {
        super(label);
    }

    public AbstractButton(SPRITE label, @Nullable CharSequence description) {
        super(label);
        hoverInfoSet(description);
    }

    public AbstractButton(SPRITE label, COLOR bgColor) {
        super(label);
        this.bgColor = bgColor;
        bg(bgColor);
    }

    public SPRITE getLabel() {
        return label;
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
        if (!isActive) {
            super.render(r, seconds, false, false, false);
        } else {
            super.render(r, seconds, isActive, isSelected, isHovered);
        }
    }

    @Override
    public void toggle() {
        selectedToggle();
        toggleAction.accept(selectedIs());
    }

    @Override
    public Element bgClear() {
        super.bgClear();
        return element();
    }

    @Override
    public Element bg(COLOR c) {
        bgColor = c;
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
