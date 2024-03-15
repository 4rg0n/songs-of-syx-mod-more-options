package com.github.argon.sos.moreoptions.game.ui;

import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;

public class Button extends AbstractButton<Void, Button> {

    public Button(CharSequence label) {
        super(label);
    }

    public Button(CharSequence label, CharSequence description) {
        super(label, description);
    }

    public Button(CharSequence label, COLOR color, @Nullable CharSequence description) {
        super(label, color, description);
    }

    @Override
    protected Button element() {
        return this;
    }
}
