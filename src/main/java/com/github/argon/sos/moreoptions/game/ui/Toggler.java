package com.github.argon.sos.moreoptions.game.ui;

import lombok.Getter;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.Map;

public class Toggler<T> extends GuiSection implements Valuable<T> {

    private final Map<String, Valuable<T>> elements;

    private Valuable<T> activeElement;

    private DIR direction;

    @Getter
    private String activeName;

    public Toggler(Map<String, Valuable<T>> elements) {
        this(elements, DIR.N);
    }

    public Toggler(Map<String, Valuable<T>> elements, DIR direction) {
        this.elements = elements;
        this.direction = direction;

        // first element in map
        activeElement = elements.values().iterator().next();
        build(elements, direction);
    }

    private void build(Map<String, Valuable<T>> elements, DIR direction) {
        GuiSection buttons = new GuiSection();
        elements.forEach((name, renderobj) -> {
            Button button = new Button(name);
            button.clickActionSet(() -> {
                toggle(name);
            });
            buttons.addRight(3, button);
        });

        switch (direction) {
            default:
            case N:
                addDownC(3, activeElement);
                addDownC(3, buttons);
                break;
            case S:
                addDownC(3, buttons);
                addDownC(3, activeElement);
                break;
            case E:
                addRightC(3, buttons);
                addRightC(3, activeElement);
                break;
            case W:
                addRightC(3, activeElement);
                addRightC(3, buttons);
                break;
        }
    }

    public void toggle(String name) {
        if (!elements.containsKey(name)) {
           return;
        }

        Valuable<T> renderobj = elements.get(name);
        renderobj.body().moveX1Y1(activeElement.body());
        activeName = name;
        activeElement = renderobj;

        clear();
        build(elements, direction);
    }

    @Override
    public T getValue() {
        return activeElement.getValue();
    }

    @Override
    public void setValue(T value) {
        activeElement.setValue(value);
    }
}
