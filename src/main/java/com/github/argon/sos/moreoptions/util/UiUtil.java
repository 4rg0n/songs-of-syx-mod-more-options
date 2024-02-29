package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public static int getMaxColumnWidth(Collection<ColumnRow> columnRows) {
        int maxWidth = 0;
        for (ColumnRow columnRow : columnRows) {
            int sectionWidth = getMaxWidth(columnRow.getColumns());

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }

    public static List<Integer> getMaxColumnWidths(Collection<ColumnRow> columnRows) {
        List<Integer> columnWidths = new ArrayList<>();

        for (ColumnRow columnRow : columnRows) {
            for (int i = 0; i < columnRow.getColumns().size(); i++) {
                GuiSection section = columnRow.getColumns().get(i);
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

    public static List<Integer> getMaxColumnWidths(Map<?, List<List<? extends GuiSection>>> columndRowsMap) {
        List<Integer> maxColumnWidths = new ArrayList<>();

        columndRowsMap.forEach((o, table) -> {
            for (List<? extends GuiSection> columns : table) {
                fillMaxColumnWidths(maxColumnWidths, columns);
            }
        });

        return maxColumnWidths;
    }

    public static List<Integer> getMaxColumnWidths(List<List<? extends GuiSection>> gridRows) {
        List<Integer> columnWidths = new ArrayList<>();

        for (List<? extends GuiSection> columns : gridRows) {
            fillMaxColumnWidths(columnWidths, columns);
        }

        return columnWidths;
    }

    public static List<Integer> fillMaxColumnWidths(List<Integer> maxColumnWidths, List<? extends GuiSection> columns) {
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

    public static int getMaxColumnHeight(Collection<ColumnRow> columnRows) {
        int maxHeight = 0;
        for (ColumnRow columnRow : columnRows) {
            int sectionHeight = getMaxHeight(columnRow.getColumns());

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

    public static @Nullable GuiSection toGuiSection(Object renderobj) {
        if (renderobj instanceof RENDEROBJ) {
            return toGuiSection((RENDEROBJ) renderobj);
        } else if (renderobj instanceof SPRITE) {
            return toGuiSection((SPRITE) renderobj);
        }

        return null;
    }

    public static GuiSection toGuiSection(SPRITE sprite) {
        RENDEROBJ.Sprite renderobj = new RENDEROBJ.Sprite(sprite);
        return toGuiSection(renderobj);
    }

    public static GuiSection toGuiSection(RENDEROBJ renderobj) {
        if (renderobj instanceof GuiSection) {
            return (GuiSection) renderobj;
         } else {
            GuiSection section = new GuiSection();
            section.add(renderobj);

            return section;
        }
    }
}
