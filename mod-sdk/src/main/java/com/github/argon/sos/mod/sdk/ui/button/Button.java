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
     * @see AbstractButton#AbstractButton(Object)
     */
    public Button(Value value) {
        super(value);
    }

    /**
     * @see AbstractButton#AbstractButton(CharSequence)
     */
    public Button(CharSequence label) {
        super(label);
    }

    /**
     * @see AbstractButton#AbstractButton(CharSequence, CharSequence)
     */
    public Button(CharSequence label, CharSequence description) {
        super(label, description);
    }

    /**
     * @see AbstractButton#AbstractButton(CharSequence, COLOR, CharSequence)
     */
    public Button(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label, color, description);
    }

    /**
     * @see AbstractButton#AbstractButton(SPRITE)
     */
    public Button(SPRITE label) {
        super(label);
    }

    /**
     * @see AbstractButton#AbstractButton(SPRITE, COLOR)
     */
    public Button(SPRITE label, COLOR color) {
        super(label, color);
    }

    /**
     * @see AbstractButton#AbstractButton(SPRITE, CharSequence)
     */
    public Button(SPRITE label, @Nullable CharSequence description) {
        super(label, description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button<Value> searchTerm(@Nullable Value searchTerm) {
        super.searchTerm(searchTerm);
        return this;
    }
}
