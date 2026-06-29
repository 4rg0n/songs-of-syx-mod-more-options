package com.github.argon.sos.mod.sdk.ui;

import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;

/**
 * A simple button with a text or icon and a description.
 *
 * @param <Value> used for searching
 */
public class Button<Value> extends AbstractButton<Value> {

    public Button(Value value) {
        super(value);
    }

    public Button(CharSequence label) {
        super(label);
    }

    public Button(CharSequence label, CharSequence description) {
        super(label, description);
    }

    public Button(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label, color, description);
    }

    public Button(SPRITE label) {
        super(label);
    }

    public Button(SPRITE label, COLOR color) {
        super(label, color);
    }

    public Button(SPRITE label, @Nullable CharSequence description) {
        super(label, description);
    }

    @Override
    public Button<Value> searchTerm(@Nullable Value searchTerm) {
        super.searchTerm(searchTerm);
        return this;
    }
}
