package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds a row with buttons and displays the associated ui element when a button is toggled
 * Used for replacing ui elements on button toggle.
 */
public class Tabulator<K, V, E extends Valuable<V>> extends GuiSection implements Valuable<V>, Resettable, Refreshable<Tabulator<K, V, E>> {

    private final Map<Toggler.Info<K>, E> tabs;
    private final boolean resetOnToggle;
    private final Toggler<K> toggler;

    private Action<Tabulator<K, V, E>> refreshAction = o -> {};

    @Getter
    private E activeTab;
    private final ClickWrapper clicker;

    public Tabulator(Map<Toggler.Info<K>, E> tabs) {
        this(tabs, DIR.N, 0, false, false);
    }

    /**
     * @param tabs list of ui elements to toggle with info for buttons
     * @param direction where shall the element be placed: DIR.N, DIR.S, DIR.E, DIR.W
     * @param margin space between element and buttons
     * @param center whether elements shall be centered
     * @param resetOnToggle whether elements shall be reset when toggling
     */
    public Tabulator(Map<Toggler.Info<K>, E> tabs, DIR direction, int margin, boolean center, boolean resetOnToggle) {
        this.tabs = tabs;
        this.resetOnToggle = resetOnToggle;

        // first element in map
        activeTab = tabs.values().iterator().next();

        toggler = new Toggler<>(tabs.keySet());
        toggler.onToggle(this::tab);

        // guarantee same width
        int maxWidth = UiUtil.getMaxWidth(tabs.values());
        int maxHeight = UiUtil.getMaxHeight(tabs.values());

        clicker = new ClickWrapper(maxWidth, maxHeight, center) {
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

    public Toggler.Info<K> getActiveInfo() {
        return toggler.getActiveInfo();
    }

    public void tab(K key) {
        tabs.entrySet().stream()
            .filter(element -> element.getKey().getKey().equals(key))
            .findFirst()
            .ifPresent(element -> {
                if (resetOnToggle) reset();
                activeTab = element.getValue();
            });
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    public V getValue() {
        return activeTab.getValue();
    }

    @Override
    public void setValue(V value) {
        activeTab.setValue(value);
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

    @Override
    public void onRefresh(Action<Tabulator<K, V, E>> refreshAction) {
        this.refreshAction = refreshAction;
    }
}
