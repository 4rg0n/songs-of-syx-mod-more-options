package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds a row with buttons and displays the associated ui element when a button is toggled
 * Used for replacing ui elements on button toggle.
 *
 * @param <Key> type of the key to identify and toggle the tab with
 * @param <Element> type of the shown element when tab is active
 * @param <Value> type of the returned and set value
 */
public class Tabulator<Key, Element extends RENDEROBJ, Value> extends Section implements
    Valuable<Value>,
    Resettable,
    Refreshable
{

    private final Map<Key, Element> tabs;
    private final boolean resetOnToggle;

    @Getter
    @Nullable
    private final Switcher<Key> menu;

    @Setter
    @Accessors(fluent = true, chain = false)
    private VoidAction refreshAction = () -> {};

    @Getter
    private Element activeTab;
    private final AbstractUISwitcher view;

    /**
     * @param tabs list of ui elements to toggle with info for buttons
     * @param direction where shall the element be placed: DIR.N, DIR.S, DIR.E, DIR.W
     * @param margin space between element and buttons
     * @param center whether elements shall be centered
     * @param resetOnToggle whether elements shall be reset when toggling
     */
    @Builder
    public Tabulator(Map<Key, Element> tabs, @Nullable Switcher<Key> tabMenu, int margin, boolean center, boolean resetOnToggle, @Nullable DIR direction) {
        assert !tabs.isEmpty() : "tabs must not be empty";
        this.tabs = tabs;
        this.resetOnToggle = resetOnToggle;
        this.menu = tabMenu;

        // toggle to active key, when set in menu
        Key firstKey = tabs.keySet().iterator().next();
        if (tabMenu != null) {
            tabMenu.clickAction(this::tab);
            Key activeKey = tabMenu.getActiveKey();

            if (activeKey != null) {
                tab(activeKey);
            } else {
                tab(firstKey);
            }
        } else {
            // activate first element in map
            tab(firstKey);
        }

        // guarantee same width
        int maxWidth = UiUtil.getMaxWidth(tabs.values());
        int maxHeight = UiUtil.getMaxHeight(tabs.values());

        view = new AbstractUISwitcher(maxWidth, maxHeight, center) {
            @Override
            protected RENDEROBJ pget() {
                return activeTab;
            }
        };

        if (direction != null) {
            switch (direction) {
                case N:
                    addDownC(0, view);
                    if (tabMenu != null) addDownC(margin, tabMenu);
                    break;
                case S:
                    if (tabMenu != null) addDownC(0, tabMenu);
                    addDownC(margin, view);
                    break;
                case E:
                    if (tabMenu != null) addRightC(0, tabMenu);
                    addRightC(margin, view);
                    break;
                case W:
                    addRightC(0, view);
                    if (tabMenu != null) addRightC(margin, tabMenu);
                    break;
                default:
                    addDownC(0, view);
                    break;
            }
        } else {
            addDownC(0, view);
        }
    }

    public void tab(@Nullable Key key) {
        if (key == null) {
            return;
        }

        tabs.entrySet().stream()
            .filter(element -> key.equals(element.getKey()))
            .findFirst()
            .ifPresent(element -> {
                if (resetOnToggle) reset();
                activeTab = element.getValue();
                if (menu != null) menu.switch_(key);
            });
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    @Nullable
    public Value getValue() {
        if (activeTab instanceof Valuable) {
            @SuppressWarnings("unchecked")
            Valuable<Value> valuable
                = (Valuable<Value>) activeTab;
            return valuable.getValue();
        } else {
            return null;
        }
    }

    @Override
    public void setValue(Value value) {
        if (activeTab instanceof Valuable) {
            @SuppressWarnings("unchecked")
            Valuable<Value> valuable
                = (Valuable<Value>) activeTab;
            valuable.setValue(value);
        }
    }

    @Override
    public void reset() {
        tabs.values().stream()
            .filter(element -> element instanceof Resettable)
            .map(Resettable.class::cast)
            .forEach(Resettable::reset);
    }

    @Override
    public void refresh() {
        view.dirty = true;
        refreshAction.accept();

        // refresh tabs if possible
        tabs.values().stream()
            .filter(element -> element instanceof Refreshable)
            .map(Refreshable.class::cast)
            .collect(Collectors.toList())
            .forEach(Refreshable::refresh);
    }
}
