package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.ui.builder.element.ScrollableBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.table.GScrollRows;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A table with a scrollbar
 */
@Getter
public class Table extends GuiSection {
    private final List<ColumnRow> rows;
    private final int displayHeight;
    private final boolean evenOdd;
    private final boolean evenColumnWidth;
    private boolean scrollable;

    public Table(
        List<ColumnRow> rows,
        int displayHeight,
        boolean scrollable,
        boolean evenOdd,
        boolean evenColumnWidth
    ) {
        this.rows = rows;
        this.displayHeight = displayHeight;
        this.scrollable = scrollable;
        this.evenOdd = evenOdd;
        this.evenColumnWidth = evenColumnWidth;
        final List<Integer> maxWidths;

        // max width for each column
        maxWidths = UiUtil.getMaxColumnWidths(rows);

        // make every column the same with?
        if (evenColumnWidth) {
            Integer maxWidth = maxWidths.stream().max(Comparator.naturalOrder())
                .orElse(null);

            if (maxWidth != null) {
                List<Integer> evenMaxWidths = maxWidths.stream()
                    .map(integer -> maxWidth)
                    .collect(Collectors.toList());

                maxWidths.clear();
                maxWidths.addAll(evenMaxWidths);
            }
        }

        // initialize columns width and even-odd background
        rows.forEach(columnRow -> {
            columnRow.init(maxWidths);
            if (evenOdd && rows.indexOf(columnRow) % 2 == 0) {
                columnRow.background(COLOR.WHITE15);
            }
        });

        int currentHeight = rows.stream()
            .mapToInt(value -> value.body().height())
            .sum();

        // add scrollbar?
        if (scrollable || (displayHeight > 0 && currentHeight > displayHeight)) {
            GScrollRows gScrollRows = ScrollableBuilder.builder()
                .height(displayHeight)
                .rows(rows)
                .build().getResult();

            add(gScrollRows.view());
        } else {
            for (ColumnRow row : rows) {
                addDown(0, row);
            }
        }
    }
}
