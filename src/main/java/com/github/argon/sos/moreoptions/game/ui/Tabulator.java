package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
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
public class Tabulator<Key, Element extends RENDEROBJ, Value> extends GuiSection implements
    Valuable<Value, Tabulator<Key, Element, Value>>,
    Resettable<Tabulator<Key, Element, Value>>,
    Refreshable<Tabulator<Key, Element, Value>> {

    private final Map<UiInfo<Key>, Element> tabs;
    private final boolean resetOnToggle;
    private final Toggler<Key> toggler;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<Tabulator<Key, Element, Value>> refreshAction = o -> {};

    @Getter
    private Element activeTab;
    private final AbstractUISwitcher clicker;

    public Tabulator(Map<UiInfo<Key>, Element> tabs) {
        this(tabs, DIR.N, 0, 0, false, false);
    }

    /**
     * @param tabs list of ui elements to toggle with info for buttons
     * @param direction where shall the element be placed: DIR.N, DIR.S, DIR.E, DIR.W
     * @param margin space between element and buttons
     * @param center whether elements shall be centered
     * @param resetOnToggle whether elements shall be reset when toggling
     */
    public Tabulator(Map<UiInfo<Key>, Element> tabs, DIR direction, int margin, int marginToggler, boolean center, boolean resetOnToggle) {
        this.tabs = tabs;
        this.resetOnToggle = resetOnToggle;

        // first element in map
        activeTab = tabs.values().iterator().next();

        toggler = new Toggler<>(new ArrayList<>(tabs.keySet()), marginToggler, false, true, true);
        toggler.clickAction(this::tab);

        // guarantee same width
        int maxWidth = UiUtil.getMaxWidth(tabs.values());
        int maxHeight = UiUtil.getMaxHeight(tabs.values());

        clicker = new AbstractUISwitcher(maxWidth, maxHeight, center) {
            @Override
            protected RENDEROBJ pget() {
                return activeTab;
            }
        };

        switch (direction) {
            default:
            case N:
                addDownC(0, clicker);
                addDownC(margin, toggler);
                break;
            case S:
                addDownC(0, toggler);
                addDownC(margin, clicker);
                break;
            case E:
                addRightC(0, toggler);
                addRightC(margin, clicker);
                break;
            case W:
                addRightC(0, clicker);
                addRightC(margin, toggler);
                break;
        }
    }

    public UiInfo<Key> getActiveInfo() {
        return toggler.getActiveInfo();
    }

    public void tab(@Nullable Key key) {
        if (key == null) {
            return;
        }

        tabs.entrySet().stream()
            .filter(element -> key.equals(element.getKey().getKey()))
            .findFirst()
            .ifPresent(element -> {
                if (resetOnToggle) reset();
                activeTab = element.getValue();
                toggler.toggle(key);
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
            Valuable<Value, Tabulator<Key, Element, Value>> valuable
                = (Valuable<Value, Tabulator<Key, Element, Value>>) activeTab;
            return valuable.getValue();
        } else {
            return null;
        }
    }

    @Override
    public void setValue(Value value) {
        if (activeTab instanceof Valuable) {
            @SuppressWarnings("unchecked")
            Valuable<Value, Tabulator<Key, Element, Value>> valuable
                = (Valuable<Value, Tabulator<Key, Element, Value>>) activeTab;
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
        clicker.dirty = true;
        refreshAction.accept(this);

        // refresh tabs if possible
        tabs.values().stream()
            .filter(element -> element instanceof Refreshable)
            .map(Refreshable.class::cast)
            .collect(Collectors.toList())
            .forEach(Refreshable::refresh);
    }
}
