package com.github.argon.sos.mod.sdk.ui.menu;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import com.github.argon.sos.mod.sdk.ui.button.AbstractButton;
import com.github.argon.sos.mod.sdk.ui.button.Button;
import com.github.argon.sos.mod.sdk.ui.input.InputString;
import com.github.argon.sos.mod.sdk.ui.layout.LayoutUtil;
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

/**
 * Will build a menu with multiple buttons
 *
 * @param <Key> type of the key used for each button in the menu
 */
public class ButtonMenu<Key> extends Section {

    @Getter
    private final Map<Key, ? extends AbstractButton<Key>> buttons;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<Key> clickAction;

    /**
     * Will create a button menu from a map with buttons
     *
     * @param buttons map with the key and there corresponding button
     */
    public ButtonMenu(Map<Key, AbstractButton<Key>> buttons) {
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
        Map<Key, AbstractButton<Key>> buttons,
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

        for (Map.Entry<Key, ? extends AbstractButton<Key>> entry : buttons.entrySet()) {
            Key key = entry.getKey();
            AbstractButton<Key> newButton = entry.getValue();

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
            InputString searchField = new InputString(search);
            addDownC(0, searchField);
            searchBarHeight = searchField.body().height();
            searchBarMargin = 10;
        }

        List<? extends RENDEROBJ> renders = LayoutUtil.align(buttons.values(), horizontal, margin, spacer, sameWidth, width, minWidth, widths);
        if (maxWidth <= 0) {
            maxWidth = UiUtil.sumWidths(renders);
        }

        if (maxHeight <= 0) {
            maxHeight = UiUtil.getSumHeight(buttons.values());
        }

        margin = (spacer) ? 0 : margin;

        if (horizontal) {
            Integer maxWidths = UiUtil.getMaxWidth(renders, margin);
            if (maxWidths > maxWidth) {
                LayoutUtil.flow(renders, this, search, maxWidth, maxHeight, margin);
            } else {
                LayoutUtil.horizontal(renders, this, margin, maxWidth, maxHeight, true);
            }
        } else {
            GuiSection vertical = LayoutUtil.vertical(renders, null, search, margin, maxHeight - searchBarHeight, true);
            addDownC(searchBarMargin, vertical);
        }
    }

    /**
     * Will look for a button with the given key in the menu.
     *
     * @param key of the button to look for
     * @return the actual found button or null
     */
    @Nullable
    public AbstractButton<Key> get(Key key) {
        return buttons.get(key);
    }

    /**
     * Overridden lombok builder with additional methods to build a button menu
     *
     * @param <Key> type of the key used for each button in the menu
     */
    public static class ButtonMenuBuilder<Key> {

        private final Map<Key, AbstractButton<Key>> buttons = new LinkedHashMap<>();

        /**
         * Creates a new {@link ButtonMenuBuilder}.
         */
        public ButtonMenuBuilder() {
        }

        /**
         * Will create a button menu from a list of buttons
         *
         * @param buttonList to create the menu from
         * @return the built button menu
         * @param <Key> type of the key used for each button in the menu
         */
        public static <Key> ButtonMenu<Key> fromList(List<? extends AbstractButton<Key>> buttonList) {
            LinkedHashMap<Key, AbstractButton<Key>> collect = buttonList.stream().collect(Collectors.toMap(
                button -> button.getValue(), button -> button,
                (left, right) -> left, LinkedHashMap::new));

            return new ButtonMenu<>(collect);
        }

        /**
         * Will add the given button map to the actual entries of the builder
         *
         * @param buttons to add
         * @return builder with added button map
         */
        public ButtonMenuBuilder<Key> buttons(Map<Key, AbstractButton<Key>> buttons) {
            this.buttons.putAll(buttons);
            return this;
        }

        /**
         * Will create buttons from the given list of keys and add them to the builder
         *
         * @param keys to add as buttons
         * @return builder with added buttons
         */
        public ButtonMenuBuilder<Key> buttons(List<Key> keys) {
            Map<Key, AbstractButton<Key>> buttonMap = new LinkedHashMap<>();
            for (Key key : keys) {
                Button<Key> button = new Button<>(key);
                button.setValue(key);
                button.searchTerm(key);
                buttonMap.put(key, button);
            }

            this.buttons.putAll(buttonMap);
            return this;
        }

        /**
         * Will add a single button with given key to the builder
         *
         * @param key of the button
         * @param button actual button to add
         * @return builder with added button
         */
        public ButtonMenuBuilder<Key> button(Key key, AbstractButton<Key> button) {
            this.buttons.put(key, button);
            return this;
        }
    }
}
