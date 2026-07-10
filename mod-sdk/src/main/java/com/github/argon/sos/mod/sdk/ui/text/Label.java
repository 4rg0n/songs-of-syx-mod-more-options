package com.github.argon.sos.mod.sdk.ui.text;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.sprite.text.Font;
import util.colors.GCOLOR;
import util.gui.misc.GText;

/**
 * A label is a text element for describing something else.
 * e.g. "Name: Jake" Where "Name" would be the label.
 */
@Builder
public class Label extends Section {

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
    private Action<GUI_BOX> hoverAction = o -> {};
    @Builder.Default
    private Style style = Style.NORMAL;

    /**
     * Creates a new {@link Label}.
     *
     * @param name what the label shall be called
     * @param maxWidth available width for displaying the label; 0 will be ignored
     * @param font optionally which font to use. Default is {@link init.sprite.UI.UIFonts#H2}
     * @param description optionally. When hovering the label with the mouse
     * @param hoverAction optionally. A way to display a custom rendered box when hovering
     * @param style optionally which style (color) to use
     */
    public Label(
        String name,
        int maxWidth,
        @Nullable Font font,
        @Nullable String description,
        @Nullable Action<GUI_BOX> hoverAction,
        @Nullable Label.Style style
    ) {
        this.name = name;
        this.maxWidth = maxWidth;
        if (font != null) this.font = font;
        if (hoverAction != null) this.hoverAction = hoverAction;
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

    /**
     * Will display a hover box with text when the label is hovered.
     *
     * @param text box with text
     */
    @Override
    public void hoverInfoGet(GUI_BOX text) {
        hoverAction.accept(text);
        super.hoverInfoGet(text);
    }

    /**
     * Different pre defined styles for the label.
     */
    public enum Style {
        /**
         * With {@link GCOLOR#T()#H1} as color.
         */
        LABEL,

        /**
         * With {@link GCOLOR#T()#WARNING} as color.
         */
        WARNING,

        /**
         * With {@link GCOLOR#T()#ERROR} as color.
         */
        ERROR,

        /**
         * With {@link GCOLOR#T()#LABEL_SUB} as color.
         */
        LABEL_SUB,

        /**
         * With {@link GCOLOR#T()#NORMAL} as color.
         */
        NORMAL,

        /**
         * With {@link GCOLOR#T()#HOVER} as color.
         */
        HOVER
    }
}
