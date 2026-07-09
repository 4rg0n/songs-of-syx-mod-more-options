package com.github.argon.sos.mod.sdk.ui.layout;

import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.ui.HorizontalLine;
import com.github.argon.sos.mod.sdk.ui.ScrollRows;
import com.github.argon.sos.mod.sdk.ui.VerticalLine;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for aligning ui elements.
 */
@UtilityClass
public class LayoutUtil {

    /**
     * Aligns given ui elements vertically.
     * Will add a scrollbar when max height is exceeded.
     *
     * @param elements to align
     * @param section optional section used as container for the ui elements
     * @param search optional search field to filter ui elements
     * @param margin space between each element
     * @param maxHeight max height of all elements when aligned
     * @param center whether the elements shall be centered
     * @return section with vertically aligned ui elements
     */
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

    /**
     * Aligns given ui elements horizontally.
     * Will start a new row of elements if max width is exceeded.
     *
     * @param elements to align
     * @param section optional section used as container for the ui elements
     * @param margin space between each element
     * @param maxWidth max width of all elements when aligned
     * @param maxHeight max height of the section
     * @param center whether the elements shall be centered
     * @return section with horizontally aligned ui elements
     */
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

        Integer maxWidths = UiUtil.getMaxWidth(elements, margin);
        if (maxWidths > maxWidth) {
            LayoutUtil.flow(elements, section,  null, maxWidth, maxHeight, margin);
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

    /**
     * Aligns given ui elements next to each other beginning from left.
     * Will start a new row of elements if max width is exceeded.
     *
     * @param elements to align
     * @param section optional section used as container for the ui elements
     * @param margin space between each element
     * @param maxWidth max width of all elements when aligned
     * @param maxHeight max height of the section
     * @return section with aligned ui elements
     */
    public static GuiSection flow(
        Collection<? extends RENDEROBJ> elements,
        @Nullable GuiSection section,
        @Nullable StringInputSprite search,
        int maxWidth,
        int maxHeight,
        int margin
    ) {
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

    /**
     * Prepares a list of given ui elements to be aligned.
     *
     * @param elements to align
     * @param horizontal whether they shall be aligned horizontally or vertically
     * @param margin space between each element
     * @param spacer whether to insert a line as spacer
     * @param sameWidth whether all elements shall have the same width
     * @param width optional width to set all elements to. 0 for ignoring.
     * @param minWidth min width of an element
     * @param widths of each element
     * @return list of prepared elements to align
     */
    public static List<? extends RENDEROBJ> align(
        Collection<? extends RENDEROBJ> elements,
        boolean horizontal,
        int margin,
        boolean spacer,
        boolean sameWidth,
        int width,
        int minWidth,
        List<Integer> widths
    ) {
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
