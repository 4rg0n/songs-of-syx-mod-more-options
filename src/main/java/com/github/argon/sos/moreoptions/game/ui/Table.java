package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.ui.builder.element.ScrollableBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.table.GScrollRows;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A table with a scrollbar
 */
@Getter
public class Table extends GuiSection implements Searchable<String, List<String>> {
    private final List<ColumnRow> rows;
    private final int displayHeight;
    private final boolean evenOdd;
    private final boolean evenColumnWidth;
    private final boolean scrollable;
    private final StringInputSprite search;

    public Table(
        List<ColumnRow> rows,
        int displayHeight,
        boolean scrollable,
        boolean evenOdd,
        boolean evenColumnWidth,
        @Nullable StringInputSprite search
    ) {
        this.rows = rows;
        this.displayHeight = displayHeight;
        this.scrollable = scrollable;
        this.evenOdd = evenOdd;
        this.evenColumnWidth = evenColumnWidth;
        this.search = search;
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
        if (search != null || scrollable || (displayHeight > 0 && currentHeight > displayHeight)) {
            GScrollRows gScrollRows = ScrollableBuilder.builder()
                .height(displayHeight)
                .rows(rows)
                .search(search)
                .build().getResult();

            addDown(0, gScrollRows.view());
        } else {
            for (ColumnRow row : rows) {
                addDown(0, row);
            }
        }
    }

    @Override
    public List<String> search(String term) {
        return rows.stream()
            .filter(columnRow -> columnRow.search(term))
            .map(ColumnRow::getSearchTerm)
            .collect(Collectors.toList());
    }
}
