package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GText;

@Getter
public class Notification extends GuiSection implements Hideable<Notification> {

    @Getter
    private final String text;
    private final String title;
    @Getter
    @Nullable
    private final COLOR titleBackground;
    private final GText displayText;

    private boolean marked = false;
    protected Action<Notification> hideAction = o -> {};

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
        Font font = UI.FONT().M;
        int textHeight = font.getHeight(text, width);
        if (textHeight > height) {
            font = UI.FONT().S;
        }

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

    @Override
    public void hide() {
        if (visableIs()) {
            hideAction.accept(this);
            visableSet(false);
        }
    }

    @Override
    public void onHide(Action<Notification> hideAction) {
        this.hideAction = hideAction;
    }
}
