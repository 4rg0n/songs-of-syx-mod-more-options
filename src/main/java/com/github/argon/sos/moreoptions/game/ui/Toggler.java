package com.github.argon.sos.moreoptions.game.ui;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.Map;

public class Toggler<T> extends GuiSection implements Valuable<T>, Resettable {

    private final Map<Info, Valuable<T>> elements;
    private final boolean resetOnToggle;

    @Getter
    private Valuable<T> activeElement;

    @Getter
    private Info activeInfo;
    private final ClickSwitch switcher;

    public Toggler(Map<Info, Valuable<T>> elements) {
        this(elements, DIR.N, false);
    }

    public Toggler(Map<Info, Valuable<T>> elements, DIR direction, boolean resetOnToggle) {
        this.elements = elements;
        this.resetOnToggle = resetOnToggle;

        // first element in map
        activeElement = elements.values().iterator().next();
        activeInfo = elements.keySet().iterator().next();

        switcher = new ClickSwitch(activeElement);

        GuiSection buttons = new GuiSection();
        elements.forEach((info, renderobj) -> {
            Button button = new Button(info.getTitle()) {
                @Override
                protected void clickA() {
                    toggle(info.getKey());
                }

                @Override
                protected void renAction() {
                    selectedSet(switcher.current().equals(elements.get(info)));
                }
            };
            button.hoverInfoSet(info.getDescription());

            buttons.addRight(3, button);
        });

        switch (direction) {
            default:
            case N:
                addDownC(3, switcher);
                addDownC(3, buttons);
                break;
            case S:
                addDownC(3, buttons);
                addDownC(3, switcher);
                break;
            case E:
                addRightC(3, buttons);
                addRightC(3, switcher);
                break;
            case W:
                addRightC(3, switcher);
                addRightC(3, buttons);
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
                switcher.set(activeElement);
            });
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
