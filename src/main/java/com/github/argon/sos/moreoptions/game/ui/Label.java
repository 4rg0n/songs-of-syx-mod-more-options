package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GText;

@Builder
public class Label extends GuiSection {

    private String name;
    @Builder.Default
    private int maxWidth = 0;
    @Builder.Default
    private Font font = UI.FONT().H2;
    @Nullable
    @Builder.Default
    private String description = null;
    @Setter
    @Builder.Default
    @Accessors(fluent = true, chain = false)
    private Action<GUI_BOX> hoverGuiAction = o -> {};
    @Builder.Default
    private Style style = Style.NORMAL;

    public Label(
        String name,
        int maxWidth,
        @Nullable Font font,
        @Nullable String description,
        @Nullable Action<GUI_BOX> hoverGuiAction,
        @Nullable Label.Style style
    ) {
        this.name = name;
        this.maxWidth = maxWidth;
        if (font != null) this.font = font;
        if (hoverGuiAction != null) this.hoverGuiAction = hoverGuiAction;
        if (description != null) this.description = description;
        if (style != null) this.style = style;

        GText text = new GText(font, name);
        switch (this.style) {
            case LABEL:
                text.lablify();
                break;
            case ERROR:
                text.errorify();
                break;
            case LABEL_SUB:
                text.lablifySub();
                break;
            case WARNING:
                text.warnify();
                break;
            case NORMAL:
                text.normalify();
                break;
            case HOVER:
                text.hoverify();
                break;
            default:
                break;
        }

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
        super.hoverInfoGet(text);
    }

    public enum Style {
        LABEL,
        WARNING,
        ERROR,
        LABEL_SUB,
        NORMAL,
        HOVER
    }
}
