package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GText;

/**
 * Serves as container for displaying in game notification popups.
 */
@Getter
public class Notification extends Section {

    @Getter
    private final String text;
    private final String title;
    @Getter
    @Nullable
    private final COLOR titleBackground;
    private final GText displayText;

    private boolean marked = false;
    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Notification> hideAction = o -> {};

    /**
     * @param text which text to display as notification
     * @param width of the content area
     * @param height of the content area
     * @param title to display in the head of the notification
     * @param titleBackground color used to highlight the title
     * @param textColor for the displayed notification text
     */
    @Builder
    public Notification(
        String text,
        int width,
        int height,
        @Nullable String title,
        @Nullable COLOR titleBackground,
        @Nullable COLOR textColor
    ) {
        this.text = text;
        this.titleBackground = titleBackground;
        body().setWidth(width);
        body().setHeight(height);
        this.title = title;

        // Use smaller font when text gets too big
        Font font = UI.FONT().H2;
        int textHeight = font.getHeight(text, width);
        if (textHeight > height) {
            font = UI.FONT().M;
        }
        textHeight = font.getHeight(text, width);
        if (textHeight > height) {
            font = UI.FONT().S;
        }

        // text preparation and coloring
        this.displayText = new GText(font, text);
        displayText.setMaxWidth(width);
        if (textColor != null) {
            displayText.color(textColor);
            marked = true;
        }

        GuiSection textContainer = new GuiSection();
        textContainer.add(displayText, 0,0);
        add(textContainer);
        textContainer.body().centerIn(body());
    }
}
