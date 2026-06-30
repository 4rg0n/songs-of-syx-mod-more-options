package com.github.argon.sos.mod.sdk.game.util;

import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import snake2d.util.datatypes.Coo;
import snake2d.util.datatypes.RECTANGLEE;
import snake2d.util.datatypes.Rec;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import view.main.VIEW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides methods for dealing with ui elements
 */
@UtilityClass
public class UiUtil {

    /**
     * Provides mouse coordinates
     */
    public final static Supplier<Coo> MOUSE_COO_SUPPLIER = () -> new Coo(VIEW.mouse().x(), VIEW.mouse().y());

    /**
     * Calculates a sum of all width from the given ui elements
     *
     * @param renderObjects list of ui elements to sum the width from
     * @return sum of all render element widths
     */
    public static int sumWidths(@Nullable Collection<? extends RENDEROBJ> renderObjects) {
        int sumWidth = 0;

        if (renderObjects == null) {
            return sumWidth;
        }

        for (RENDEROBJ renderobj : renderObjects) {
            sumWidth += renderobj.body().width();
        }

        return sumWidth;
    }

    /**
     * Determines the wides width of a ui element from a given list.
     *
     * @param renderObjects list of ui elements to get the max width from
     * @return width of widest ui element in list
     */
    public static int getMaxWidth(@Nullable Collection<? extends RENDEROBJ> renderObjects) {
        int maxWidth = 0;

        if (renderObjects == null) {
            return maxWidth;
        }

        for (RENDEROBJ renderobj : renderObjects) {
            int currentWidth = renderobj.body().width();

            if (currentWidth > maxWidth) {
                maxWidth = currentWidth;
            }
        }

        return maxWidth;
    }

    /**
     * Determines the widest width of a {@link ColumnRow} from a given list.
     *
     * @param columnRows list of column rows to get the max width from
     * @return width of widest ui element in a column row
     * @param <Value> type of the value in the column rows
     */
    public static <Value> int getMaxColumnWidth(@Nullable Collection<ColumnRow<Value>> columnRows) {
        int maxWidth = 0;

        if (columnRows == null) {
            return maxWidth;
        }

        for (ColumnRow<Value> columnRow : columnRows) {
            int sectionWidth = getMaxWidth(columnRow.getColumns());

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }

    /**
     * Determines the max margin of a {@link ColumnRow} from a given list.
     *
     * @param columnRows list of column rows to get the max margin from
     * @return max margin
     * @param <Value> type of the value in the column rows
     */
    public static <Value> int getMaxColumnMargin(@Nullable Collection<ColumnRow<Value>> columnRows) {
        int margin = 0;

        if (columnRows == null) {
            return margin;
        }

        for (ColumnRow<Value> columnRow : columnRows) {
            if (margin < columnRow.margin()) {
                margin = columnRow.margin();
            }
        }

        return margin;
    }

    /**
     * Determines the widths of the widest column in each row.
     *
     * @param columnRows to get the max widths from
     * @return list of widths of the widest column in each row
     * @param <Value> type of the value in the column rows
     */
    public static <Value> List<Integer> getMaxColumnWidths(@Nullable Collection<ColumnRow<Value>> columnRows) {
        if (columnRows == null) {
            return List.of();
        }

        List<Integer> columnWidths = new ArrayList<>();

        for (ColumnRow<?> columnRow : columnRows) {
            if (columnRow.isHeader()) {
                // skip header rows
                continue;
            }

            for (int i = 0; i < columnRow.getColumns().size(); i++) {
                GuiSection column = columnRow.getColumns().get(i);
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

    /**
     * Determines the widest width of a row with all its columns
     *
     * @param grid to get the widest width from
     * @return the widest width of a row with all its columns
     */
    public static int getMaxCombinedColumnWidth(@Nullable List<List<? extends GuiSection>> grid) {
        int combinedWidth = 0;

        if (grid == null) {
            return combinedWidth;
        }

        for (List<? extends GuiSection> columns : grid) {
            List<Integer> columnWidths = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                GuiSection column = columns.get(i);
                int width = column.body().width();

                if (columnWidths.size() <= i) {
                    columnWidths.add(width);
                } else if (columnWidths.get(i) < width) {
                    columnWidths.set(i, width);
                }
            }

            int combined = 0;
            for (Integer columnWidth : columnWidths) {
                combined += columnWidth;
            }

            if(combined > combinedWidth) {
                combinedWidth = combined;
            }

        }

        return combinedWidth;
    }

    /**
     * Determines a list with max column widths in a map with grids
     *
     * @param grids to get the widths from
     * @return list with max column widths
     */
    public static List<Integer> getMaxColumnWidths(@Nullable Map<?, List<List<? extends GuiSection>>> grids) {
        if (grids == null) {
            return List.of();
        }

        List<Integer> maxColumnWidths = new ArrayList<>();
        grids.forEach((o, table) -> {
            for (List<? extends GuiSection> columns : table) {
                fillMaxColumnWidths(maxColumnWidths, columns);
            }
        });

        return maxColumnWidths;
    }

    /**
     * Determines a list with max column widths in a grid
     *
     * @param grid to get the widths from
     * @return list with max column widths
     */
    public static List<Integer> getMaxColumnWidths(@Nullable List<List<? extends GuiSection>> grid) {
        if (grid == null) {
            return List.of();
        }

        List<Integer> columnWidths = new ArrayList<>();
        for (List<? extends GuiSection> columns : grid) {
            fillMaxColumnWidths(columnWidths, columns);
        }

        return columnWidths;
    }

    /**
     * Determines the max height from a list of column rows.
     *
     * @param columnRows to get the max height from
     * @return max height
     * @param <Value> type of the value in the column rows
     */
    public static <Value> int getMaxColumnHeight(@Nullable Collection<ColumnRow<Value>> columnRows) {
        int maxHeight = 0;

        if (columnRows == null) {
            return maxHeight;
        }

        for (ColumnRow<Value> columnRow : columnRows) {
            int sectionHeight = getMaxHeight(columnRow.getColumns());

            if (sectionHeight > maxHeight) {
                maxHeight = sectionHeight;
            }
        }

        return maxHeight;
    }

    /**
     * Determines the max height from a list of ui elements
     *
     * @param renders to get max height from
     * @return max height
     */
    public static int getMaxHeight(@Nullable Collection<? extends RENDEROBJ> renders) {
        int maxHeight = 0;

        if (renders == null) {
            return maxHeight;
        }

        for (RENDEROBJ section : renders) {
            int sectionHeight = section.body().height();

            if (sectionHeight > maxHeight) {
                maxHeight = sectionHeight;
            }
        }

        return maxHeight;
    }

    /**
     * Sums the height of all ui elements in a list.
     *
     * @param renders to calculate the sum height from
     * @return summed height
     */
    public static int getSumHeight(@Nullable Collection<? extends RENDEROBJ> renders) {
        int sumHeight = 0;

        if (renders == null) {
            return sumHeight;
        }

        for (RENDEROBJ section : renders) {
            int sectionHeight = section.body().height();
            sumHeight += sectionHeight;
        }

        return sumHeight;
    }

    /**
     * Determines the widest width from a list of ui elements with margin
     *
     * @param renderObjects to get the max width from
     * @param margin for each ui element
     * @return determined max width
     */
    public static Integer getMaxWidth(Collection<? extends RENDEROBJ> renderObjects, int margin) {
        return getWidths(renderObjects).stream().mapToInt(Integer::intValue).sum()
            + (renderObjects.size() - 1) * margin;
    }

    /**
     * Collects the widths from all given ui elements.
     *
     * @param renderObjects to collect widths from
     * @return collected widths
     */
    public static List<Integer> getWidths(Collection<? extends RENDEROBJ> renderObjects) {
        return renderObjects.stream()
            .map(value -> value.body().width())
            .toList();
    }

    /**
     * Sums height from all given ui elements.
     *
     * @param renderObjects to sum height from
     * @return summed height
     */
    public static int sumHeights(Collection<? extends RENDEROBJ> renderObjects) {
        return renderObjects.stream()
            .mapToInt(value -> value.body().height())
            .sum();
    }

    /**
     * Extracts a resizeable body from an ui element.
     *
     * @param element to get body from
     * @return resizable body or null
     */
    @Nullable
    public static Rec getResizeableBody(RENDEROBJ element) {
        RECTANGLEE body = element.body();

        if (body instanceof Rec) {
            return (Rec) body;
        }

        return null;
    }

    private static List<Integer> fillMaxColumnWidths(List<Integer> maxColumnWidths, List<? extends GuiSection> columns) {
        for (int i = 0; i < columns.size(); i++) {
            GuiSection column = columns.get(i);
            int width = column.body().width();

            if (maxColumnWidths.size() <= i) {
                maxColumnWidths.add(width);
            } else if (maxColumnWidths.get(i) < width) {
                maxColumnWidths.set(i, width);
            }
        }

        return maxColumnWidths;
    }
}
