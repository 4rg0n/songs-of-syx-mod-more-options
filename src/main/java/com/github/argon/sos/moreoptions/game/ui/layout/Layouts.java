package com.github.argon.sos.moreoptions.game.ui.layout;

import com.github.argon.sos.moreoptions.game.ui.HorizontalLine;
import com.github.argon.sos.moreoptions.game.ui.ScrollRows;
import com.github.argon.sos.moreoptions.game.ui.VerticalLine;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Layouts {

    public static GuiSection vertical(
        Collection<? extends RENDEROBJ> elements,
        int margin,
        boolean spacer
    ) {
        return align(elements, null, margin, false, spacer, false, false, 0, 0, null);
    }

    public static GuiSection vertical(int margin, boolean center, RENDEROBJ... renders) {
        GuiSection section = new GuiSection();

        for (int i = 0; i < renders.length; i++) {
            RENDEROBJ render = renders[i];
            addDown(section, margin, render, center);
        }

        return section;
    }

    public static GuiSection horizontal(int margin, boolean center, RENDEROBJ... renders) {
        GuiSection section = new GuiSection();

        for (int i = 0; i < renders.length; i++) {
            RENDEROBJ render = renders[i];
            addRight(section, margin, render, center);
        }

        return section;
    }

    public static GuiSection vertical(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        int margin,
        boolean center,
        boolean spacer,
        boolean sameWidth,
        int width,
        int minWidth,
        @Nullable List<Integer> widths
    ) {
        return align(elements, section, margin, center, spacer, sameWidth, false, width, minWidth, widths);
    }

    public static GuiSection horizontal(
        Collection<? extends RENDEROBJ> elements,
        int margin,
        boolean spacer
    ) {
        return align(elements, null, margin, false, spacer, false, true, 0, 0, null);
    }

    public static GuiSection horizontal(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        int margin,
        boolean center,
        boolean spacer,
        boolean sameWidth,
        int width,
        int minWidth,
        @Nullable List<Integer> widths
    ) {
        return align(elements, section, margin, center, spacer, sameWidth, true, width, minWidth, widths);
    }

    private static GuiSection align(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        int margin,
        boolean center,
        boolean spacer,
        boolean sameWidth,
        boolean horizontal,
        int width,
        int minWidth,
        @Nullable List<Integer> widths
    ) {
        if (section == null) {
            section = new GuiSection();
        }

        int elementsMaxWidth = UiUtil.getMaxWidth(elements);
        int elementsMaxHeight = UiUtil.getMaxHeight(elements);
        int maxWidth = 0;
        if (widths == null) maxWidth = elementsMaxWidth;

        int pos = 0;
        for (RENDEROBJ element : elements) {
            Rec elementBody = UiUtil.getResizeableBody(element);

            if (elementBody != null) {
                int elementWidth;
                if (sameWidth && maxWidth > 0) {
                    // adjust with by widest
                    elementWidth = maxWidth;
                } else if (widths != null && pos < widths.size()) {
                    // adjust width by given widths
                    elementWidth = widths.get(pos);
                } else if (width > 0) {
                    // set all elements to the same given width
                    elementWidth = width;
                } else {
                    // use the element width
                    elementWidth = element.body().width();
                }

                if (minWidth > 0 && elementWidth < minWidth) {
                    elementWidth = minWidth;
                }

                elementBody.setWidth(elementWidth);
            }

            if (horizontal) {
                if (spacer) {
                    RENDEROBJ line = new VerticalLine( margin + 1, elementsMaxHeight, 1);
                    if (pos > 0) {
                        addRight(section,0, line, center);
                    }
                    addRight(section,0, element, center);
                } else {
                    addRight(section, margin, element, center);
                }
            } else {
                if (spacer) {
                    RENDEROBJ line = new HorizontalLine(elementsMaxWidth, margin + 1, 1);
                    if (pos > 0) {
                        addDown(section,0, line, center);
                    }
                    addDown(section,0, element, center);
                } else {
                    addDown(section, margin, element, center);
                }
            }

            pos++;
        }

        return section;
    }

    public static GuiSection flow(Collection<? extends RENDEROBJ> elements, @Nullable GuiSection section, int maxWidth, int maxHeight, int margin) {
        if (section == null) {
            section = new GuiSection();
        }

        GuiSection currentRow = new GuiSection();
        int widthCounter = 0;

        List<GuiSection> rows = new ArrayList<>();
        rows.add(currentRow);

        for (RENDEROBJ element : elements) {
            if (widthCounter + element.body().width() + margin > maxWidth) {
                currentRow = new GuiSection();
                rows.add(currentRow);
            }

            currentRow.addRight(margin, element);
            widthCounter = currentRow.body().width();
        }

        int currentHeight = UiUtil.sumHeights(rows);

        if (maxHeight > 0 && currentHeight > maxHeight) {
            ScrollRows scrollRows = ScrollRows.builder()
                .height(maxHeight)
                .rows(rows)
                .slide(true)
                .build();

            section.add(scrollRows.view());
        } else {
            for (GuiSection row : rows) {
                section.addDown(margin, row);
            }
        }

        return section;
    }

    private static void addRight(GuiSection section, int margin, RENDEROBJ element, boolean center) {
        if (center) {
            section.addRightC(margin, element);
        } else {
            section.addRight(margin, element);
        }
    }

    private static void addDown(GuiSection section, int margin, RENDEROBJ element, boolean center) {
        if (center) {
            section.addDownC(margin, element);
        } else {
            section.addDown(margin, element);
        }
    }
}
