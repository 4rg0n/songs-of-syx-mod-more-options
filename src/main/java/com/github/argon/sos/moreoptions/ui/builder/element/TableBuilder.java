package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.table.GScrollRows;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds a scrollable list out of {@link GridRow}s
 */
@Builder
@RequiredArgsConstructor
public class TableBuilder implements UiBuilder<GuiSection, GScrollRows> {

    private final List<List<? extends GuiSection>> rows;

    private final int displayHeight;

    private final boolean evenOdd;

    public BuildResult<GuiSection, GScrollRows> build() {

        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(rows);
        List<GridRow> gridRows = rows.stream()
            .map(columns -> {
                GridRow gridRow = new GridRow(columns);
                gridRow.initGrid(maxWidths);

                if (evenOdd && rows.indexOf(columns) % 2 == 0) {
                    gridRow.background(COLOR.WHITE15);
                }

                return gridRow;
            })
            .collect(Collectors.toList());

        GScrollRows gScrollRows = ScrollableBuilder.builder()
            .height(displayHeight)
            .rows(gridRows)
            .build().getResult();

        GuiSection section = new GuiSection();
        section.add(gScrollRows.view());

        return BuildResult.<GuiSection, GScrollRows>builder()
            .interactable(gScrollRows)
            .result(section)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private List<List<? extends GuiSection>> rows;

        @lombok.Setter
        @Accessors(fluent = true)
        private int displayHeight;

        @lombok.Setter
        @Accessors(fluent = true)
        private boolean evenOdd = false;

        public BuildResult<GuiSection, GScrollRows> build() {
            assert rows != null : "rows must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new TableBuilder(rows, displayHeight, evenOdd).build();
        }
    }
}
