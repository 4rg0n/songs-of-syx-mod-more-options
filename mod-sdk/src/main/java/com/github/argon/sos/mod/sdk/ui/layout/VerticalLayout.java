package com.github.argon.sos.mod.sdk.ui.layout;

import lombok.*;
import snake2d.util.datatypes.BODY_HOLDER;
import snake2d.util.datatypes.DIMENSION;
import snake2d.util.datatypes.RECTANGLE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * TODO: this fails if you don't have a simple row layout, but rows with multiple scalable columns in it too
 */
public class VerticalLayout {
    private final int maxHeight;

    private int height;

    private int width;

    private int scalableCount;

    public VerticalLayout(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    private final List<Element> elements = new ArrayList<>();

    public VerticalLayout addDown(int margin, RENDEROBJ renderobj) {
        elements.add(Element.builder()
            .center(false)
            .uiElement(renderobj)
            .margin(margin)
            .build());
        add(renderobj);
        addHeight(margin);

        return this;
    }

    public VerticalLayout addDown(int margin, SPRITE sprite) {
        elements.add(Element.builder()
            .center(false)
            .uiElement(sprite)
            .margin(margin)
            .build());
        add(sprite);
        addHeight(margin);

        return this;
    }

    public VerticalLayout addDown(int margin, Scalable scalable) {
        elements.add(Element.builder()
            .center(false)
            .uiElement(scalable)
            .margin(margin)
            .build());
        add(scalable);
        addHeight(margin);
        scalableCount++;

        return this;
    }

    public VerticalLayout addDownC(int margin, RENDEROBJ renderobj) {
        elements.add(Element.builder()
            .center(true)
            .uiElement(renderobj)
            .margin(margin)
            .build());
        add(renderobj);
        addHeight(margin);

        return this;
    }

    public VerticalLayout addDownC(int margin, SPRITE sprite) {
        elements.add(Element.builder()
            .center(true)
            .uiElement(sprite)
            .margin(margin)
            .build());
        add(sprite);
        addHeight(margin);

        return this;
    }

    public VerticalLayout addDownC(int margin, Scalable scalable) {
        elements.add(Element.builder()
            .center(true)
            .uiElement(scalable)
            .margin(margin)
            .build());
        add(scalable);
        addHeight(margin);
        scalableCount++;

        return this;
    }

    public <Section extends GuiSection> Scalables build(Section section) {
        Scalables scalables = new Scalables();

        if (!doesHeightFit()) {
            noSpace(section);
            return scalables;
        }

        final int leftoverHeight = getHeightLeftoverFor(scalableCount);
        elements.stream().peek(element -> {
            if (element.getUiElement() instanceof Scalable) {
                Scalable scalable = (Scalable) element.getUiElement();
                RENDEROBJ renderobj = scalable.getBuildAction().apply(scalable.getMinHeight() + leftoverHeight);
                element.setUiElement(renderobj);
                scalables.add(renderobj);
            }
        }).forEach(element -> {
            Object uiElement = element.getUiElement();
            if (element.isCenter()) {
                if (uiElement instanceof RENDEROBJ) {
                    section.addDownC(element.getMargin(), (RENDEROBJ) uiElement);
                } else if (uiElement instanceof SPRITE) {
                    section.addDownC(element.getMargin(), (SPRITE) uiElement);
                }
            } else {
                if (uiElement instanceof RENDEROBJ) {
                    section.addDown(element.getMargin(), (RENDEROBJ) uiElement);
                } else if (uiElement instanceof SPRITE) {
                    section.addDown(element.getMargin(), (SPRITE) uiElement);
                }
            }
        });

        return scalables;
    }

    protected void addHeight(int height) {
        this.height += height;
    }

    protected void addWidth(int width) {
        if (width > this.width) {
            this.width = width;
        }
    }

    protected void add(DIMENSION dimension) {
        addHeight(dimension.height());
        addWidth(dimension.width());
    }

    protected void add(Scalable element) {
        addHeight(element.getMinHeight());
    }

    protected void add(RECTANGLE rectangle) {
        addHeight(rectangle.height());
        addWidth(rectangle.width());
    }

    protected void add(BODY_HOLDER bodyHolder) {
        add(bodyHolder.body());
    }

    private <Section extends GuiSection> Section noSpace(Section section) {
        section.addDownC(0, Layout.NO_SPACE);
        return section;
    }

    protected boolean doesHeightFit() {
        return height <= maxHeight;
    }

    public VerticalLayout clear() {
        height = 0;
        width = 0;
        scalableCount = 0;
        elements.clear();

        return this;
    }

    protected int getHeightLeftover() {
        int leftover = maxHeight - height;

        return Math.max(leftover, 0);
    }

    protected int getHeightLeftoverFor(int amount) {
        int leftover = getHeightLeftover();

        if (leftover == 0) {
            return 0;
        }

        if (amount == 0) {
            return leftover;
        }

        return leftover / amount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    protected static class Element {
        private boolean center;
        private Object uiElement;
        private int margin;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Scalable {
        private final int minHeight;
        private final Function<Integer, ? extends RENDEROBJ> buildAction;
    }

    /**
     * TODO I don't like this... it exists, because I sometimes need the build result of the created scalable element
     */
    public static class Scalables {
        private final List<RENDEROBJ> all = new ArrayList<>();

        public boolean add(RENDEROBJ renderobj) {
            return all.add(renderobj);
        }

        public <T> T getAs(int index) {
            //noinspection unchecked
            return (T) all.get(index);
        }
    }
}
