package com.github.argon.sos.moreoptions.game.ui;

import init.sprite.UI.UI;
import lombok.Builder;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GText;

@Builder
public class Label extends GuiSection {
    @Builder.Default
    private Font font = UI.FONT().M;
    private String name;
    private String description;
    @Builder.Default
    private int maxWidth = 0;

    public Label(Font font, String name, String description, int maxWidth) {
        this.font = font;
        this.name = name;
        this.description = description;
        this.maxWidth = maxWidth;

        GText text = new GText(font, name).lablify();
        addRight(0, text);

        if (maxWidth > 0) {
            text.setMaxWidth(maxWidth);
            body().setWidth(maxWidth);
        }

        if (description != null) {
            hoverInfoSet(description);
        }
    }
}
