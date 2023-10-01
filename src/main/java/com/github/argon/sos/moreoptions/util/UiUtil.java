package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.game.ui.GridRow;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.Collection;

public class UiUtil {
    public static int getMaxWidthNested(Collection<? extends Collection<? extends RENDEROBJ>> sections) {
        int maxWidth = 0;

        for (Collection<? extends RENDEROBJ> nestedSections : sections) {
            int sectionWidth = getMaxWidth(nestedSections);

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }
    public static int getMaxWidth(Collection<? extends RENDEROBJ> sections) {
        int maxWidth = 0;

        for (RENDEROBJ section : sections) {
            int sectionWidth = section.body().width();

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }

    public static int getMaxColumnWidth(Collection<GridRow> gridRows) {
        int maxWidth = 0;
        for (GridRow gridRow : gridRows) {
            int sectionWidth = getMaxWidth(gridRow.getColumns());

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }

    public static int getMaxColumnHeight(Collection<GridRow> gridRows) {
        int maxHeight = 0;
        for (GridRow gridRow : gridRows) {
            int sectionHeight = getMaxHeight(gridRow.getColumns());

            if (sectionHeight > maxHeight) {
                maxHeight = sectionHeight;
            }
        }

        return maxHeight;
    }

    public static int getMaxHeightNested(Collection<? extends Collection<? extends RENDEROBJ>> sections) {
        int maxHeight = 0;

        for (Collection<? extends RENDEROBJ> nestedSections : sections) {
            int sectionHeight = getMaxHeight(nestedSections);

            if (sectionHeight > maxHeight) {
                maxHeight = sectionHeight;
            }
        }

        return maxHeight;
    }

    public static int getMaxHeight(Collection<? extends RENDEROBJ> sections) {
        int maxHeight = 0;

        for (RENDEROBJ section : sections) {
            int sectionHeight = section.body().height();

            if (sectionHeight > maxHeight) {
                maxHeight = sectionHeight;
            }
        }

        return maxHeight;
    }
}
