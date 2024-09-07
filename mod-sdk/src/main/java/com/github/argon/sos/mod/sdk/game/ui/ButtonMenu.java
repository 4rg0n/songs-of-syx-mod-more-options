package com.github.argon.sos.mod.sdk.game.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.ui.layout.Layouts;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ButtonMenu<Key> extends Section {

    @Getter
    private final Map<Key, Button> buttons;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction;

    public ButtonMenu(Map<Key, Button> buttons) {
        this(buttons, false, true, false, false, false, false, 0, 0, 0, 0, 0, COLOR.WHITE35, null, null, null);
    }

    /**
     * Builds a menu out of buttons. The menu can be vertically or horizontally aligned.
     *
     * @param buttons key value list with the buttons of the menu
     * @param horizontal whether the menu shall be displayed horizontal (depends on maxWidth)
     * @param sameWidth whether each button shall have the same width. Determined by the biggest button width.
     * @param notClickable whether buttons shall be not clickable
     * @param notHoverable whether buttons shall be not hoverable
     * @param spacer whether buttons shall have a spacer rendered between them
     * @param displaySearch whether a default search bar shall be displayed
     * @param margin space between each button in px
     * @param width fixed with for each button; 0 ignores this
     * @param minWidth min width of buttons
     * @param maxWidth max width of the whole button menu
     * @param maxHeight max height of the whole button menu
     * @param buttonColor color for all buttons
     * @param widths list of widths for each button
     * @param clickAction register an action when the button is clicked
     * @param search custom search bar to use
     */
    @Builder
    public ButtonMenu(
        Map<Key, Button> buttons,
        boolean horizontal,
        boolean sameWidth,
        boolean notClickable,
        boolean notHoverable,
        boolean spacer,
        boolean displaySearch,
        int margin,
        int width,
        int minWidth,
        int maxWidth,
        int maxHeight,
        @Nullable COLOR buttonColor,
        @Nullable List<Integer> widths,
        @Nullable Action<Key> clickAction,
        @Nullable StringInputSprite search
    ) {
        assert !buttons.isEmpty() : "buttons must not be empty";
        this.buttons = buttons;
        this.clickAction = clickAction;

        for (Map.Entry<Key, Button> entry : buttons.entrySet()) {
            Key key = entry.getKey();
            Button newButton = entry.getValue();

            newButton.clickable(!notClickable);
            newButton.hoverable(!notHoverable);
            if (buttonColor != null) newButton.bg(buttonColor);
            if (this.clickAction != null) {
                newButton.clickActionSet(() -> {
                    if (this.clickAction != null) this.clickAction.accept(key);
                });
            }
        }

        int searchBarHeight = 0;
        int searchBarMargin = 0;
        if (displaySearch) {
            if (search == null) {
                search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
            }
            Input searchField = new Input(search);
            addDownC(0, searchField);
            searchBarHeight = searchField.body().height();
            searchBarMargin = 10;
        }

        List<? extends RENDEROBJ> renders = Layouts.align(buttons.values(), horizontal, margin, spacer, sameWidth, width, minWidth, widths);
        maxWidth = (maxWidth > 0) ? maxWidth : 300;
        maxHeight = (maxHeight > 0) ? maxHeight : 300;
        margin = (spacer) ? 0 : margin;

        if (horizontal) {
            Integer maxWidths = UiUtil.getMaxWidths(renders, margin);
            if (maxWidths > maxWidth) {
                Layouts.flow(renders, this, search, maxWidth, maxHeight, margin);
            } else {
                Layouts.horizontal(renders, this, margin, maxWidth, maxHeight, true);
            }
        } else {
            GuiSection vertical = Layouts.vertical(renders, null, search, margin, maxHeight - searchBarHeight, true);
            addDownC(searchBarMargin, vertical);
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
            Map<Key, Button> buttonMap = new LinkedHashMap<>();
            for (Key name : names) {

                if (name instanceof String) {
                    String key = (String) name;
                    Button button = new Button(key);
                    button.setValue(key);
                    button.searchTerm(key);
                    buttonMap.put(name, button);
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
