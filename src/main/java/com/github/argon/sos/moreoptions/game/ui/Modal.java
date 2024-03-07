package com.github.argon.sos.moreoptions.game.ui;

import snake2d.Renderer;
import snake2d.util.gui.GuiSection;

/**
 * For displaying a {@link GuiSection} in a modal window.
 * The background is blacked.
 *
 * @param <Section> ui element to display
 */
public class Modal<Section extends GuiSection> extends Window<Section> {
    public Modal(String title, Section section) {
        super(title, section);
        center();
    }

    @Override
    protected boolean render(Renderer renderer, float v) {
        super.render(renderer, v);
        return false; // no background aka black
    }
}
