package com.github.argon.sos.mod.sdk.ui.layout;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.BODY_HOLDER;
import snake2d.util.datatypes.DIMENSION;
import snake2d.util.datatypes.RECTANGLE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Used or vertically aligning ui elements.
 *
 * FIXME: this fails if you don't have a simple row layout, but rows with multiple scalable columns in it too
 */
public class VerticalLayout implements Resettable {
    private final int maxHeight;

    private int height;

    private int width;

    private int scalableCount;

    private final List<Element> elements = new ArrayList<>();

    /**
     * Creates a new {@link VerticalLayout} with given max height.
     *
     * @param maxHeight of the layout
     */
    public VerticalLayout(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * Adds a {@link RENDEROBJ} under the last element.
     *
     * @param margin space between this and last element
     * @param renderobj to add
     * @return this
     */
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

    /**
     * Adds a {@link SPRITE} under the last ui element.
     *
     * @param margin space between this and last element
     * @param sprite to add
     * @return this
     */
    public VerticalLayout addDown(int margin, SPRITE sprite) {
        elements.add(Element.builder()
            .center(false)
            .uiElement(sprite)
            .margin(margin)
            .build());
        addDimension(sprite);
        addHeight(margin);

        return this;
    }

    /**
     * Adds a {@link Scalable} element under the last element.
     *
     * @param margin space between this and last element
     * @param scalable to add
     * @return this
     */
    public VerticalLayout addDown(int margin, Scalable scalable) {
        elements.add(Element.builder()
            .center(false)
            .uiElement(scalable)
            .margin(margin)
            .build());
        addHeight(scalable);
        addHeight(margin);
        scalableCount++;

        return this;
    }

    /**
     * Adds a {@link RENDEROBJ} element under the last element and centers it horizontally.
     *
     * @param margin space between this and last element
     * @param renderobj to add
     * @return this
     */
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

    /**
     * Adds a {@link SPRITE} element under the last element and centers it horizontally.
     *
     * @param margin space between this and last element
     * @param sprite to add
     * @return this
     */
    public VerticalLayout addDownC(int margin, SPRITE sprite) {
        elements.add(Element.builder()
            .center(true)
            .uiElement(sprite)
            .margin(margin)
            .build());
        addDimension(sprite);
        addHeight(margin);

        return this;
    }

    /**
     * Adds a {@link Scalable} element under the last element and centers it horizontally.
     *
     * @param margin space between this and last element
     * @param scalable to add
     * @return this
     */
    public VerticalLayout addDownC(int margin, Scalable scalable) {
        elements.add(Element.builder()
            .center(true)
            .uiElement(scalable)
            .margin(margin)
            .build());
        addHeight(scalable);
        addHeight(margin);
        scalableCount++;

        return this;
    }

    /**
     * Builds a {@link Scalable} with a new section containing {@link VerticalLayout#elements}.
     *
     * @return scalable with elements
     * @see VerticalLayout#build(GuiSection)
     */
    public Scalables build() {
        return build(null);
    }

    /**
     * Builds a {@link Scalable} with a section containing {@link VerticalLayout#elements}.
     *
     * @param inputSection optional section to add the elements into. If no section is given, a new one will be created.
     * @return scalable with elements
     */
    public Scalables build(@Nullable final GuiSection inputSection) {
        final Scalables scalables = new Scalables();

        final GuiSection section = Optional.ofNullable(inputSection)
            .orElse(new Section());

        if (!doesHeightFit()) {
            scalables.add(noSpace(section));
            return scalables;
        }

        final int leftoverHeight = getHeightLeftoverFor(scalableCount);
        elements.stream().peek(element -> {
            if (element.getUiElement() instanceof Scalable scalable) {
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

    /**
     * Adds height to {@link VerticalLayout#height}.
     *
     * @param height to add
     */
    protected void addHeight(int height) {
        this.height += height;
    }

    /**
     * Adds width to {@link VerticalLayout#width}.
     *
     * @param width to add
     */
    protected void addWidth(int width) {
        if (width > this.width) {
            this.width = width;
        }
    }

    /**
     * Adds height and width to {@link VerticalLayout#height} and {@link VerticalLayout#width} from given {@link DIMENSION}.
     *
     * @param dimension with height and width to add
     */
    protected void addDimension(DIMENSION dimension) {
        addHeight(dimension.height());
        addWidth(dimension.width());
    }

    /**
     * Adds height to {@link VerticalLayout#height} from given {@link Scalable}
     *
     * @param element with height to add
     */
    protected void addHeight(Scalable element) {
        addHeight(element.getMinHeight());
    }

    /**
     * Adds height and width to {@link VerticalLayout#height} and {@link VerticalLayout#width} from given {@link RECTANGLE}.
     *
     * @param rectangle with height and width to add
     */
    protected void add(RECTANGLE rectangle) {
        addHeight(rectangle.height());
        addWidth(rectangle.width());
    }

    /**
     * Adds height and width to {@link VerticalLayout#height} and {@link VerticalLayout#width} from given {@link BODY_HOLDER}.
     *
     * @param bodyHolder with height and width to add
     */
    protected void add(BODY_HOLDER bodyHolder) {
        add(bodyHolder.body());
    }

    private GuiSection noSpace(GuiSection section) {
        section.addDownC(0, Layout.NO_SPACE);
        return section;
    }

    /**
     * Checks whether another element would fit.
     *
     * @return whether another element would fit
     */
    protected boolean doesHeightFit() {
        return height <= maxHeight;
    }

    /**
     * Clears everything from the layout.
     */
    public void reset() {
        height = 0;
        width = 0;
        scalableCount = 0;
        elements.clear();
    }

    /**
     * Returns how much space is left.
     *
     * @return how much space is left.
     */
    protected int getHeightLeftover() {
        int leftover = maxHeight - height;

        return Math.max(leftover, 0);
    }

    /**
     * Returns how much height each element would have at max.
     *
     * @param elementAmount amount of elements to check for
     * @return how much height each element would have at max
     */
    protected int getHeightLeftoverFor(int elementAmount) {
        int leftover = getHeightLeftover();

        if (leftover == 0) {
            return 0;
        }

        if (elementAmount == 0) {
            return leftover;
        }

        return leftover / elementAmount;
    }

    /**
     * Container for an ui element and its settings.
     */
    @Data
    @Builder
    @AllArgsConstructor
    protected static class Element {
        private boolean center;
        private Object uiElement;
        private int margin;
    }

    /**
     * Container for a ui element which can be scaled.
     * Contains a build action, which knows how to create the element.
     */
    @Getter
    @RequiredArgsConstructor
    public static class Scalable {
        private final int minHeight;
        private final Function<Integer, ? extends RENDEROBJ> buildAction;
    }

    /**
     * Container for multiple ui elements.
     */
    public static class Scalables {
        @Getter
        private final List<RENDEROBJ> uiElements = new ArrayList<>();

        /**
         * Creates a new {@link Scalables}.
         */
        public Scalables() {
        }

        /**
         * Adds a new ui element.
         *
         * @param uiElement to add
         * @return whether the ui elements list has changed
         */
        public boolean add(RENDEROBJ uiElement) {
            return uiElements.add(uiElement);
        }

        /**
         * Returns the ui element at the given index cast to the requested type.
         *
         * @param index of the ui element
         * @return ui element at the given index
         * @param <T> type to cast the ui element to
         */
        public <T> T getAs(int index) {
            //noinspection unchecked
            return (T) uiElements.get(index);
        }
    }
}
