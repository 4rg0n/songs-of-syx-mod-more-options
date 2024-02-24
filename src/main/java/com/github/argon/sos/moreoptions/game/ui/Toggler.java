package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.ACTION;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.misc.ACTION.ACTION_O;

import java.util.Collection;
import java.util.Optional;

/**
 * Builds a row with buttons to toggle
 */
public class Toggler<K> extends GuiSection implements Valuable<K>, Resettable {

    private final Collection<Info<K>> elements;

    @Getter
    private Info<K> activeInfo;

    @Getter
    private Button activeButton;

    @SuppressWarnings("unchecked")
    private ACTION_O<K> toggleAction = ACTION.NOPO;

    public Toggler(Collection<Info<K>> elements) {
        this(elements, 0);
    }

    /**
     * @param elements list of info elements used for building the buttons
     * @param margin space between buttons
     */
    public Toggler(Collection<Info<K>> elements, int margin) {
        this.elements = elements;

        // first element in map
        activeInfo = elements.iterator().next();

        elements.forEach(info -> {
            Button button = new Button(info.getTitle()) {
                @Override
                protected void clickA() {
                    activeButton = this;
                    toggle(info.getKey());
                }

                @Override
                protected void renAction() {
                    boolean selected = info.equals(activeInfo);

                    selectedSet(selected);
                }
            };
            button.hoverInfoSet(info.getDescription());
            addRight(margin, button);
        });
    }

    public void toggle(K key) {
        get(key).ifPresent(element -> {
            activeInfo = element;
            toggleAction.exe(key);
        });
    }

    public void onToggle(ACTION_O<K> action) {
        toggleAction = action;
    }

    public Optional<Info<K>> get(K key) {
        return elements.stream()
            .filter(element -> element.getKey().equals(key))
            .findFirst();
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    public K getValue() {
        return activeInfo.getKey();
    }

    @Override
    public void setValue(K value) {
        toggle(value);
    }

    @Override
    public void reset() {
        elements.stream()
            .filter(element -> element instanceof Resettable)
            .map(Resettable.class::cast)
            .forEach(Resettable::reset);
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Info<T> {

        private final T key;
        private final String title;

        private final String description;
    }
}
