package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.ui.layout.Layouts;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// todo maxHeight / width and make scrollable
public class ButtonMenu<Key> extends GuiSection {

    @Getter
    private final Map<Key, Button> buttons;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction;

    public ButtonMenu(Map<Key, Button> buttons) {
        this(buttons, false, true, false, false, false, 0, 0, 0, COLOR.WHITE35, null, null);
    }

    @Builder
    public ButtonMenu(
        Map<Key, Button> buttons,
        boolean horizontal,
        boolean sameWidth,
        boolean notClickable,
        boolean notHoverable,
        boolean spacer,
        int margin,
        int width,
        int minWidth,
        @Nullable COLOR buttonColor,
        @Nullable List<Integer> widths,
        @Nullable Action<Key> clickAction
    ) {
        this.buttons = buttons;
        this.clickAction = clickAction;

        for (Map.Entry<Key, Button> entry : buttons.entrySet()) {
            Key key = entry.getKey();
            Button newButton = entry.getValue();

            newButton.clickable(!notClickable);
            newButton.hoverable(!notHoverable);
            if (buttonColor != null) newButton.bg(buttonColor);
            if (clickAction != null) {
                newButton.clickActionSet(() -> {
                    clickAction.accept(key);
                });
            }
        }

        if (horizontal) {
            Layouts.horizontal(buttons.values(), this, margin, true, spacer, sameWidth, width, minWidth, widths);
        } else {
            Layouts.vertical(buttons.values(), this, margin, true, spacer, sameWidth, width, minWidth, widths);
        }
    }

    public Button get(Key key) {
        return buttons.get(key);
    }

    public static class ButtonMenuBuilder<Key> {

        private final Map<Key, Button> buttons = new LinkedHashMap<>();

        public static ButtonMenu<String> fromList(List<Button> infoList) {
            LinkedHashMap<String, Button> collect = infoList.stream().collect(Collectors.toMap(
                Object::toString, button -> button,
                (left, right) -> left, LinkedHashMap::new));

            return new ButtonMenu<>(collect);
        }

        public ButtonMenuBuilder<Key> buttons(Map<Key, Button> buttons) {
            this.buttons.putAll(buttons);
            return this;
        }

        public ButtonMenuBuilder<Key> button(Key key, Button button) {
            this.buttons.put(key, button);
            return this;
        }
    }
}
