package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.gui.GUI_BOX;
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

    @Setter
    @Builder.Default
    @Accessors(fluent = true, chain = false)
    private Action<GUI_BOX> hoverGuiAction = o -> {};

    public Label(Font font, String name, String description, int maxWidth, Action<GUI_BOX> hoverGuiAction) {
        this.font = font;
        this.name = name;
        this.description = description;
        this.maxWidth = maxWidth;
        this.hoverGuiAction = hoverGuiAction;

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

    @Override
    public void hoverInfoGet(GUI_BOX text) {
        hoverGuiAction.accept(text);
    }
}
