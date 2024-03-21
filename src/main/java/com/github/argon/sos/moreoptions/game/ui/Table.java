package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.moreoptions.util.Lists;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A table with a scrollbar
 */
@Getter
@Builder
public class Table<Value> extends GuiSection implements
    Searchable<String, List<String>>,
    Selectable<Integer, ColumnRow<Value>>,
    Valuable<Map<String, Value>, Table<Value>> {

    private final Map<String, ColumnRow<Value>> rows;
    @Builder.Default
    private int displayHeight = 100;
    @Builder.Default
    private boolean evenColumnWidth = false;
    @Builder.Default
    private boolean evenOdd = true;
    @Builder.Default
    private boolean scrollable = false;
    @Builder.Default
    private boolean selectable = false;
    @Builder.Default
    private boolean multiselect = false;
    @Builder.Default
    private boolean highlight = false;
    @Builder.Default
    private int rowPadding = 0;
    @Builder.Default
    private int columnMargin = 0;

    @Nullable
    @Builder.Default
    private Map<String, Button> headerButtons = null;

    @Nullable
    @Builder.Default
    private StringInputSprite search = null;

    public Table(
        List<ColumnRow<Value>> rows,
        int displayHeight,
        boolean evenColumnWidth,
        boolean evenOdd,
        boolean scrollable,
        boolean selectable,
        boolean multiselect,
        boolean highlight,
        int rowPadding,
        int columnMargin,
        @Nullable Map<String, Button> headerButtons,
        @Nullable StringInputSprite search
    ) {
        assert (!rows.isEmpty()) : "rows must not be empty";
        this.rows = rows.stream().collect(Collectors.toMap(ColumnRow::getKey, row -> row));
        this.displayHeight = displayHeight;
        this.scrollable = scrollable;
        this.evenOdd = evenOdd;
        this.evenColumnWidth = evenColumnWidth;
        this.multiselect = multiselect;
        this.selectable = selectable;
        this.highlight = highlight;
        this.headerButtons = headerButtons;
        this.search = search;
        this.rowPadding = rowPadding;
        this.columnMargin = columnMargin;

        // max width and margin for each column
        final List<Integer> maxWidths = UiUtil.getMaxColumnWidths(rows);

        // prepare header if present
        if (headerButtons != null) {
            List<Integer> headerButtonWidths = UiUtil.getWidths(headerButtons.values());

            // adjust max width when header columns are wider
            for (int i = 0, headerButtonWidthsSize = headerButtonWidths.size(); i < headerButtonWidthsSize; i++) {
                Integer buttonWith = headerButtonWidths.get(i);

                if (i < maxWidths.size() && buttonWith > maxWidths.get(i)) {
                    maxWidths.set(i, buttonWith);
                }
            }

            headerButtons.values().forEach(button -> button.bg(COLOR.WHITE15));
        }

        // make every column the same with?
        if (evenColumnWidth) {
            Integer maxWidth = maxWidths.stream().max(Comparator.naturalOrder())
                .orElse(null);

            if (maxWidth != null) {
                Integer[] newMaxWidths = new Integer[maxWidths.size()];
                Arrays.fill(newMaxWidths, maxWidth);

                maxWidths.clear();
                maxWidths.addAll(Arrays.asList(newMaxWidths));
            }
        }

        int evenOddCounter = 0;
        boolean lastWasHeader = false;
        // initialize rows
        for (int i = 0, rowsSize = rows.size(); i < rowsSize; i++) {
            ColumnRow<Value> columnRow = rows.get(i);
            columnRow.margin(columnMargin);
            columnRow.init(maxWidths);
            columnRow.pad(rowPadding);
            columnRow.selectable(selectable);
            if (!columnRow.isHeader()) columnRow.highlightable(highlight);

            // unselect other rows on click?
            if (selectable && !multiselect) {
                columnRow.clickAction(clickedRow -> {
                    rows.stream()
                        .filter(row -> !row.equals(clickedRow))
                        // unselect
                        .forEach(notClickedRow -> notClickedRow.selectedSet(false));
                });
            }

            // make sure next row after header will be colored and don't color header
            if (evenOdd && columnRow.isHeader()) {
                if (evenOddCounter % 2 == 0) {
                    evenOddCounter -= 1;
                }
            }

            if (evenOdd && evenOddCounter % 2 == 0) {
                columnRow.backgroundColor(COLOR.WHITE15);
            }

            evenOddCounter++;
        }

        // add header if needed
        if (headerButtons != null) {
            List<Integer> buttonMaxWidths = maxWidths;
            // compensate button width with padding
            if (rowPadding > 0) {
                buttonMaxWidths = maxWidths.stream().map(maxWidth -> {
                    int rowPaddingAdj = (int) Math.ceil((double) rowPadding * 2 / headerButtons.size()) + columnMargin;
                    return rowPaddingAdj + maxWidth;
                }).collect(Collectors.toList());
            }

            ButtonMenu<String> header = ButtonMenu.<String>builder()
                .buttons(headerButtons)
                .horizontal(true)
                .notClickable(true)
                .notHoverable(true)
                .widths(buttonMaxWidths)
                .build();

            addDown(0, header);
        }

        // add the rows and scrollbar if needed
        int currentHeight = UiUtil.sumHeights(rows);
        if (search != null || scrollable || (displayHeight > 0 && currentHeight > displayHeight)) {
            ScrollRows scrollRows = ScrollRows.builder()
                .height(displayHeight)
                .rows(rows)
                .slide(true)
                .search(search)
                .build();

            addDown(0, scrollRows.view());
        } else {
            for (ColumnRow<Value> row : rows) {
                addDown(0, row);
            }
        }
    }

    @Override
    public List<String> search(String term) {
        return rows.values().stream()
            .filter(columnRow -> columnRow.search(term))
            .map(ColumnRow::searchTerm)
            .collect(Collectors.toList());
    }

    @Override
    public void select(List<Integer> keys) {
        for (Integer index : keys) {
            if (index >= keys.size() - 1) {
                rows.get(index).selectedSet(true);
            }
        }
    }

    @Override
    public List<ColumnRow<Value>> getSelection() {
        return rows.values().stream()
            .filter(GuiSection::selectedIs)
            .collect(Collectors.toList());
    }

    public void doubleClickAction(Action<ColumnRow<Value>> doubleClickAction) {
        rows.forEach((key, row) -> row.doubleClickAction(doubleClickAction));
    }

    public void clickAction(Action<ColumnRow<Value>> clickAction) {
        rows.forEach((key, row) -> row.clickAction(clickAction));
    }

    @Override
    public Map<String, Value> getValue() {
        return rows.values().stream()
            .filter(row -> !row.isHeader())
            .collect(Collectors.toMap(
                ColumnRow::getKey,
                ColumnRow::getValue
            ));
    }

    @Override
    public void setValue(Map<String, Value> values) {
        values.forEach((key, value) -> {
            ColumnRow<Value> row = rows.get(key);

            if (row != null) {
                row.setValue(value);
            }
        });
    }

    public static class TableBuilder<Value> {

        private List<ColumnRow<Value>> rows = new ArrayList<>();

        public TableBuilder<Value> category(String title, List<ColumnRow<Value>> rows) {
            GHeader header = new GHeader(title, UI.FONT().H2);
            GuiSection headerSection = UiUtil.toGuiSection(header);
            headerSection.pad(10);
            ColumnRow<Value> headerRow = ColumnRow.<Value>builder()
                .columns(Lists.of(headerSection))
                .build();
            headerRow.isHeader(true);
            this.rows.add(headerRow);
            this.rows.addAll(rows);

            return this;
        }

        public TableBuilder<Value> rowsCategorized(Map<String, List<ColumnRow<Value>>> rowsCategorized) {
            rowsCategorized.forEach(this::category);
            return this;
        }

        public TableBuilder<Value> rowsWithColumns(List<List<GuiSection>> rows) {
            this.rows = rows.stream()
                .map((List<GuiSection> row) -> {
                    return ColumnRow.<Value>builder()
                        .columns(row)
                        .build();
                })
                .collect(Collectors.toList());

            return this;
        }

        public TableBuilder<Value> rows(List<ColumnRow<Value>> rows) {
            this.rows = rows;
            return this;
        }

        public TableBuilder<Value> row(ColumnRow<Value> row) {
            this.rows.add(row);
            return this;
        }
    }
}
