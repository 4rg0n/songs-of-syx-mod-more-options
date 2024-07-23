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
import snake2d.util.sprite.text.StringInputSprite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Layouts {

    public static GuiSection vertical(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        @Nullable StringInputSprite search,
        int margin,
        int maxHeight,
        boolean center
    ) {
        if (section == null) {
            section = new GuiSection();
        }

        int currentHeight = UiUtil.sumHeights(elements);
        if (maxHeight > 0 && currentHeight > maxHeight) {
            ScrollRows scrollRows = ScrollRows.builder()
                .height(maxHeight)
                .rows(elements)
                .search(search)
                .slide(true)
                .build();

            section.add(scrollRows.view());
        } else {
            for (RENDEROBJ element : elements) {
                addDown(section, margin, element, center);
            }
        }

        return section;
    }

    public static GuiSection horizontal(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        int margin,
        int maxWidth,
        int maxHeight,
        boolean center
    ) {
        if (section == null) {
            section = new GuiSection();
        }

        Integer maxWidths = UiUtil.getMaxWidths(elements, margin);
        if (maxWidths > maxWidth) {
            Layouts.flow(elements, section,  null, maxWidth, maxHeight, margin);
        } else {
            int pos = 0;
            for (RENDEROBJ element : elements) {
                if (pos == 0) {
                    addRight(section,0, element, center);
                } else {
                    addRight(section, margin, element, center);
                }

                pos++;
            }
        }

        return section;
    }

    public static GuiSection flow(Collection<? extends RENDEROBJ> elements, @Nullable GuiSection section, @Nullable StringInputSprite search, int maxWidth, int maxHeight, int margin) {
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
                .search(search)
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


    public static List<? extends RENDEROBJ> align(Collection<? extends RENDEROBJ> elements, boolean horizontal, int margin, boolean spacer, boolean sameWidth, int width, int minWidth, List<Integer> widths) {
        int elementsMaxWidth = UiUtil.getMaxWidth(elements);
        int elementsMaxHeight = UiUtil.getMaxHeight(elements);
        int maxWidth = 0;
        if (widths == null) maxWidth = elementsMaxWidth;
        List<RENDEROBJ> renders = new ArrayList<>();

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

            renders.add(element);

            if (spacer && pos < elements.size() - 1) {
                RENDEROBJ line;
                if (horizontal) {
                    line = new VerticalLine( margin + 1, elementsMaxHeight, 1);
                } else {
                    line = new HorizontalLine(elementsMaxWidth, margin + 1, 1);
                }

                renders.add(line);
            }

            pos++;
        }

        return renders;
    }
}
