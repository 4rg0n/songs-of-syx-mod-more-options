package com.github.argon.sos.mod.sdk.ui.button;

import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;

/**
 * A simple button with a text or icon and a description.
 *
 * @param <Value> used for searching
 */
public class Button<Value> extends AbstractButton<Value> {

    /**
     * For creating a button with a value only.
     *
     * @param value of the button
     * @see AbstractButton#AbstractButton(Object)
     */
    public Button(Value value) {
        super(value);
    }

    /**
     * For creating a button with a label.
     *
     * @param label to display on the button
     * @see AbstractButton#AbstractButton(CharSequence)
     */
    public Button(CharSequence label) {
        super(label);
    }

    /**
     * For creating a button with a label and a description.
     *
     * @param label to display on the button
     * @param description to display when hovering
     * @see AbstractButton#AbstractButton(CharSequence, CharSequence)
     */
    public Button(CharSequence label, CharSequence description) {
        super(label, description);
    }

    /**
     * For creating a button with a label, a background color and an optional description.
     *
     * @param label to display on the button
     * @param color background color
     * @param description to display when hovering
     * @see AbstractButton#AbstractButton(CharSequence, COLOR, CharSequence)
     */
    public Button(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label, color, description);
    }

    /**
     * For creating a button with an image.
     *
     * @param label icon to show on the button
     * @see AbstractButton#AbstractButton(SPRITE)
     */
    public Button(SPRITE label) {
        super(label);
    }

    /**
     * For creating a button with an image and a background color.
     *
     * @param label icon to show on the button
     * @param color background color
     * @see AbstractButton#AbstractButton(SPRITE, COLOR)
     */
    public Button(SPRITE label, COLOR color) {
        super(label, color);
    }

    /**
     * For creating a button with an image and an optional description.
     *
     * @param label icon to show on the button
     * @param description to display when hovering
     * @see AbstractButton#AbstractButton(SPRITE, CharSequence)
     */
    public Button(SPRITE label, @Nullable CharSequence description) {
        super(label, description);
    }

    /**
     * Sets the term used for filtering this button when searching.
     *
     * @param searchTerm to search for
     * @return this button
     */
    @Override
    public Button<Value> searchTerm(@Nullable Value searchTerm) {
        super.searchTerm(searchTerm);
        return this;
    }
}
