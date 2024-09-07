package com.github.argon.sos.mod.sdk.game.ui;

import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.sprite.SPRITE;

/**
 * A simple button with a text or icon and a description.
 */
public class Button extends AbstractButton<String, Button> {

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
    protected Button element() {
        return this;
    }

    @Override
    public Boolean search(String s) {
        if (searchTerm == null) {
            return true;
        }

        return searchTerm.toLowerCase().contains(s.toLowerCase());
    }
}
