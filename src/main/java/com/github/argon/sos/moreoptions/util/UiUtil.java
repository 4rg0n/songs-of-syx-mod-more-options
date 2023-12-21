package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.game.ui.GridRow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UiUtil {
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

    public static List<Integer> getMaxColumnWidths(Collection<GridRow> gridRows) {
        List<Integer> columnWidths = new ArrayList<>();

        for (GridRow gridRow : gridRows) {
            for (int i = 0; i < gridRow.getColumns().size(); i++) {
                GuiSection section = gridRow.getColumns().get(i);
                int width = section.body().width();

                if (columnWidths.size() <= i) {
                    columnWidths.add(width);
                } else if (columnWidths.get(i) < width) {
                    columnWidths.set(i, width);
                }
            }
        }

        return columnWidths;
    }

    public static List<Integer> getMaxColumnWidths(List<List<? extends GuiSection>> gridRows) {
        List<Integer> columnWidths = new ArrayList<>();

        for (List<? extends GuiSection> columns : gridRows) {
            for (int i = 0; i < columns.size(); i++) {
                GuiSection column = columns.get(i);
                int width = column.body().width();

                if (columnWidths.size() <= i) {
                    columnWidths.add(width);
                } else if (columnWidths.get(i) < width) {
                    columnWidths.set(i, width);
                }
            }
        }

        return columnWidths;
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
