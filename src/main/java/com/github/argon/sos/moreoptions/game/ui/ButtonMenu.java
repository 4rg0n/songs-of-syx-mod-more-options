package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.*;
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
        this(buttons, false, true, true, true, false, 0, 0, 0, COLOR.WHITE35, null, null);
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
        this.buttons = new HashMap<>();
        this.clickAction = clickAction;

        int maxWidth = 0;
        if (widths == null) maxWidth = UiUtil.getMaxWidth(buttons.values());

        for (Map.Entry<Key, Button> entry : buttons.entrySet()) {
            Key key = entry.getKey();
            Button newButton = entry.getValue();

            newButton.clickable(!notClickable);
            newButton.hoverable(!notHoverable);
            if (buttonColor != null) newButton.bg(buttonColor);

            int pos = this.buttons.size();
            int buttonWidth;
            if (sameWidth && maxWidth > 0) {
                // adjust with by widest
                buttonWidth = maxWidth;
            } else if (widths != null && pos < widths.size()) {
                // adjust width by given widths
                buttonWidth = widths.get(pos);
            } else if (width > 0) {
                // set all buttons to the same given width
                buttonWidth = width;
            } else {
                // use the new button width
                buttonWidth = newButton.body().width();
            }

            if (minWidth > 0 && buttonWidth < minWidth) {
                buttonWidth = minWidth;
            }

            newButton.body().setWidth(buttonWidth);

            // add buttons in correct directions
            if (horizontal) {
                if (spacer) {
                    if (!this.buttons.isEmpty()) {
                        addRightC(0, new VerticalLine(margin - 1, newButton.body().height(), 1));
                    }
                    addRightC(0, newButton);
                } else {
                    addRightC(margin, newButton);
                }
            } else {

                if (spacer) {
                    if (!this.buttons.isEmpty()) {
                        addDownC(0, new VerticalLine(margin - 1, newButton.body().height(), 1));
                    }
                    addDownC(0, newButton);
                } else {
                    addDownC(margin, newButton);
                }
            }

            if (clickAction != null) {
                newButton.clickActionSet(() -> {
                    clickAction.accept(key);
                });
            }

            this.buttons.put(key, newButton);
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
