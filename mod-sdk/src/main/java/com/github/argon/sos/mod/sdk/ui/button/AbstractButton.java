package com.github.argon.sos.mod.sdk.ui.button;

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
 */
public abstract class AbstractButton<Value> extends GButt.ButtPanel
    implements Valuable<Value>,
    Searchable<Value, Boolean>,
    Toggleable<Boolean>
{
    /**
     * Background color used for the button
     */
    @Getter
    protected COLOR bgColor = COLOR.WHITE35;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Boolean> toggleAction = o -> {};

    /**
     * Holds the value of the button. This is usually the button name.
     */
    @Getter
    @Setter
    protected Value value;

    /**
     * Whether the button can be clicked or not
     */
    @Setter
    @Accessors(fluent = true)
    protected boolean clickable = true;

    /**
     * Whether the button will react when hovered with the mouse
     */
    @Setter
    @Accessors(fluent = true)
    protected boolean hoverable = true;

    /**
     * Will be called when the button is rendered
     */
    @Nullable
    protected ACTION renAction;

    /**
     * Used for filtering buttons when searching
     */
    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    protected Value searchTerm;

    /**
     * The default background color for the button
     */
    public final static COLOR DEFAULT_BG_COLOR = COLOR.WHITE35;

    /**
     * The default font for the button
     */
    public final static Font DEFAULT_FONT = UI.FONT().H2;

    /**
     * For creating a button with a value only.
     *
     * @param value of the button. The {@link Object#toString()} result will be used as label.
     */
    public AbstractButton(Value value) {
        this(value.toString());
        this.value = value;
    }

    /**
     * For creating a button with a label and a font to be used for it.
     * {@link AbstractButton#DEFAULT_BG_COLOR} will be used as background color.
     *
     * @param label to display on the button
     * @param font to use for the label
     */
    public AbstractButton(CharSequence label, Font font) {
        this(label, DEFAULT_BG_COLOR, font, null);
    }

    /**
     * For creating a button with a label.
     * {@link AbstractButton#DEFAULT_BG_COLOR} will be used as background color.
     * {@link AbstractButton#DEFAULT_FONT} will be used as font.
     *
     * @param label to display on the button
     */
    public AbstractButton(CharSequence label) {
        this(label, DEFAULT_BG_COLOR, DEFAULT_FONT, null);
    }

    /**
     * For creating a button with a label, an optional description and a font for the label.
     * {@link AbstractButton#DEFAULT_BG_COLOR} will be used as background color.
     *
     * @param label to display on the button
     * @param description to display when hovering
     * @param font to use for the label
     */
    public AbstractButton(CharSequence label, @Nullable CharSequence description, Font font) {
        this(label, DEFAULT_BG_COLOR, font , description);
    }

    /**
     * For creating a button with a label, the background color and an optional description.
     * {@link AbstractButton#DEFAULT_BG_COLOR} will be used as background color.
     *
     * @param label to display on the button
     * @param bgColor background color
     * @param description to display when hovering
     */
    public AbstractButton(CharSequence label, COLOR bgColor, @Nullable CharSequence description) {
        this(label, bgColor, DEFAULT_FONT, description);
    }

    /**
     * For creating a button with a label and an optional description.
     * {@link AbstractButton#DEFAULT_BG_COLOR} will be used as background color.
     * {@link AbstractButton#DEFAULT_FONT} will be used as font.
     *
     * @param label to display on the button
     * @param description to display when hovering
     */
    public AbstractButton(CharSequence label, @Nullable CharSequence description) {
        this(label, DEFAULT_BG_COLOR, DEFAULT_FONT, description);
    }

    /**
     * For creating a button with a label, the font for the label, the background color and an optional description.
     *
     * @param label to display on the button
     * @param bgColor background color
     * @param font to use for the label
     * @param description to display when hovering
     */
    public AbstractButton(CharSequence label, COLOR bgColor, Font font, @Nullable CharSequence description) {
        super(font.getText(label));
        this.bgColor = bgColor;
        bg(bgColor);
        hoverInfoSet(description);
    }

    /**
     * For creating a button with an image.
     *
     * @param icon to show on the button
     */
    public AbstractButton(SPRITE icon) {
        super(icon);
    }

    /**
     * For creating a button with an image and an optional description.
     *
     * @param icon to show on the button
     * @param description to display when hovering
     */
    public AbstractButton(SPRITE icon, @Nullable CharSequence description) {
        super(icon);
        hoverInfoSet(description);
    }

    /**
     * For creating a button with an image and the background color.
     *
     * @param icon to show on the button
     * @param bgColor background color
     */
    public AbstractButton(SPRITE icon, COLOR bgColor) {
        super(icon);
        this.bgColor = bgColor;
        bg(bgColor);
    }

    /**
     * Will return the label / name of the button
     *
     * @return label as a sprite
     */
    public SPRITE getLabel() {
        return label;
    }

    /**
     * Executed when a button is clicked
     *
     * @return whether the button is clickable
     */
    @Override
    public boolean click() {
        if (!clickable) return false;
        return super.click();
    }

    /**
     * Executed when a button is hovered
     *
     * @param mouseCoordinates coordinates of the hovering mouse
     * @return whether the button is hover-able
     */
    @Override
    public boolean hover(COORDINATE mouseCoordinates) {
        if (!hoverable) return false;
        return super.hover(mouseCoordinates);
    }

    /**
     * Executed when a button is rendered.
     *
     * @param renderer for rendering the button
     * @param deltaSeconds since the last render loop
     * @param isActive whether the button is clickable
     * @param isSelected whether the button is in a selected state
     * @param isHovered whether the button is hovered
     */
    @Override
    protected void render(SPRITE_RENDERER renderer, float deltaSeconds, boolean isActive, boolean isSelected, boolean isHovered) {
        if (!isActive) {
            super.render(renderer, deltaSeconds, false, false, false);
        } else {
            super.render(renderer, deltaSeconds, isActive, isSelected, isHovered);
        }
    }

    /**
     * Will switch the button "selected" state on or off
     */
    @Override
    public void toggle() {
        selectedToggle();
        toggleAction.accept(selectedIs());
    }

    /**
     * Clears the background color.
     *
     * @return this
     */
    @Override
    public AbstractButton<Value> bgClear() {
        super.bgClear();
        return this;
    }

    /**
     * Sets the background color.
     *
     * @param color to set
     * @return this
     */
    @Override
    public AbstractButton<Value> bg(COLOR color) {
        bgColor = color;
        super.bg(color);
        return this;
    }

    /**
     * Executes the {@link AbstractButton#renAction} when present.
     */
    @Override
    protected void renAction() {
        if (renAction != null) renAction.exe();
        super.renAction();
    }

    /**
     * Will set a render action.
     * 
     * @param action to be called when rendering
     */
    public void renActionSet(ACTION action) {
        this.renAction = action;
    }

    /**
     * In the case of the given value and {@link AbstractButton#searchTerm} being a {@link String} both will be lowercased and checked with {@link Object#equals(Object)}.
     * If not, both will be checked just with {@link Object#equals(Object)}
     * 
     * @param value to search for
     * @return whether the term was found
     */
    @Override
    public Boolean search(Value value) {
        if (searchTerm == null) {
            return true;
        }

        if (searchTerm instanceof String searchTermToUse && value instanceof String valueToSearch) {
            return searchTermToUse.toLowerCase().contains(valueToSearch.toLowerCase());
        } else {
            return searchTerm.equals(value);
        }
    }
}
