package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.UiUtil;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ButtonMenu extends GuiSection {
    private final Map<String, Button> buttons;

    public ButtonMenu(List<Button> infoList) {
        this(infoList.stream().collect(Collectors.toMap(
            Object::toString,
            button -> button)));
    }

    public ButtonMenu(Map<String, Button> buttons) {
        this(buttons, 0);
    }

    public ButtonMenu(Map<String, Button> buttons, int margin) {
        this.buttons = buttons;
        int maxWidth = UiUtil.getMaxWidth(buttons.values());

        this.buttons.values().forEach(button -> {
            button.body().setWidth(maxWidth);
            addDown(margin, button);
        });
    }

    public Button get(String key) {
        return buttons.get(key);
    }
}
