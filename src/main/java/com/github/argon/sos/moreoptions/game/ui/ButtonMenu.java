package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.ui.layout.Layouts;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.HashMap;
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
        this(buttons, false, true, false, false, false, 0, 0, 0, 0, 0, COLOR.WHITE35, null, null);
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
        int maxWidth,
        int maxHeight,
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

        List<? extends RENDEROBJ> renders = Layouts.align(buttons.values(), horizontal, margin, spacer, sameWidth, width, minWidth, widths);
        maxWidth = (maxWidth > 0) ? maxWidth : 300;
        maxHeight = (maxHeight > 0) ? maxHeight : 300;
        margin = (spacer) ? 0 : margin;

        if (horizontal) {
            Integer maxWidths = UiUtil.getMaxWidths(renders, margin);
            if (maxWidths > maxWidth) {
                Layouts.flow(renders, this, maxWidth, maxHeight, margin);
            } else {
                Layouts.horizontal(renders, this, margin, maxWidth, maxHeight, true);
            }
        } else {
            Layouts.vertical(renders, this, margin, maxHeight, true);
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

        public ButtonMenuBuilder<Key> buttons(List<Key> names) {
            Map<Key, Button> buttonMap = new HashMap<>();
            for (Key name : names) {

                if (name instanceof String) {
                    String key = (String) name;
                    buttonMap.put(name, new Button(key));
                }
            }

            this.buttons.putAll(buttonMap);
            return this;
        }

        public ButtonMenuBuilder<Key> button(Key key, Button button) {
            this.buttons.put(key, button);
            return this;
        }
    }
}
