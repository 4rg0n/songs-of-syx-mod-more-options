package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UiUtil {

    public static Slider.ValueDisplay fromDisplayMode(MoreOptionsConfig.Range.DisplayMode displayMode) {
            switch (displayMode) {
                case PERCENTAGE:
                    return Slider.ValueDisplay.PERCENTAGE;
                case ABSOLUTE:
                    return Slider.ValueDisplay.ABSOLUTE;
                default:
                case NONE:
                    return Slider.ValueDisplay.NONE;
            }
    }

    public static int getMaxWidth(Collection<? extends RENDEROBJ> renderobjs) {
        int maxWidth = 0;

        for (RENDEROBJ renderobj : renderobjs) {
            int currentWidth = renderobj.body().width();

            if (currentWidth > maxWidth) {
                maxWidth = currentWidth;
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

    public static Integer getMaxCombinedColumnWidth(List<List<? extends GuiSection>> gridRows) {
        int combinedWidth = 0;


        for (List<? extends GuiSection> columns : gridRows) {
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
