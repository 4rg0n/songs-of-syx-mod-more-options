package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.Map;

/**
 * Builds a row with buttons and displays the associated ui element when a button is toggled
 * Used for replacing ui elements on button toggle.
 */
public class Toggler<T> extends GuiSection implements Valuable<T>, Resettable {

    private final Map<Info, Valuable<T>> elements;
    private final boolean resetOnToggle;

    @Getter
    private Valuable<T> activeElement;

    @Getter
    private Info activeInfo;

    public Toggler(Map<Info, Valuable<T>> elements) {
        this(elements, DIR.N, 0, false, false);
    }

    /**
     * @param elements list of ui elements to toggle with info for buttons
     * @param direction where shall the element be placed: DIR.N, DIR.S, DIR.E, DIR.W
     * @param margin space between element and buttons
     * @param center whether elements shall be centered
     * @param resetOnToggle whether elements shall be reset when toggling
     */
    public Toggler(Map<Info, Valuable<T>> elements, DIR direction, int margin, boolean center, boolean resetOnToggle) {
        this.elements = elements;
        this.resetOnToggle = resetOnToggle;

        // first element in map
        activeElement = elements.values().iterator().next();
        activeInfo = elements.keySet().iterator().next();

        GuiSection buttons = new GuiSection();
        elements.forEach((info, renderobj) -> {
            Button button = new Button(info.getTitle()) {
                @Override
                protected void clickA() {
                    toggle(info.getKey());
                }

                @Override
                protected void renAction() {
                    selectedSet(activeElement.equals(elements.get(info)));
                }
            };
            button.hoverInfoSet(info.getDescription());
            buttons.addRight(margin, button);
        });

        // guarantee same width
        int maxWidth = UiUtil.getMaxWidth(elements.values());
        int maxHeight = UiUtil.getMaxHeight(elements.values());

        ClickWrapper clicker = new ClickWrapper(maxWidth, maxHeight, center) {
            @Override
            protected RENDEROBJ pget() {
                return activeElement;
            }
        };

        switch (direction) {
            default:
            case N:
                addDownC(0, clicker);
                addDownC(margin, buttons);
                break;
            case S:
                addDownC(0, buttons);
                addDownC(margin, clicker);
                break;
            case E:
                addRightC(0, buttons);
                addRightC(margin, clicker);
                break;
            case W:
                addRightC(0, clicker);
                addRightC(margin, buttons);
                break;
        }
    }

    public void toggle(String key) {
        elements.entrySet().stream()
            .filter(element -> element.getKey().getKey().equals(key))
            .findFirst()
            .ifPresent(element -> {
                if (resetOnToggle) reset();

                Valuable<T> renderobj = element.getValue();
                activeInfo = element.getKey();
                activeElement = renderobj;
            });
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        super.render(r, ds);
    }

    @Override
    public T getValue() {
        return activeElement.getValue();
    }

    @Override
    public void setValue(T value) {
        activeElement.setValue(value);
    }

    @Override
    public void reset() {
        elements.values().stream()
            .filter(element -> element instanceof Resettable)
            .map(Resettable.class::cast)
            .forEach(Resettable::reset);
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Info {

        private final String key;
        private final String title;

        private final String description;
    }
}
