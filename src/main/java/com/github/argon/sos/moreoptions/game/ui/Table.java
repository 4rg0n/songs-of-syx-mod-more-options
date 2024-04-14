package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.*;
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
public class Table<Value> extends ColorBox implements
    Searchable<String, List<String>>,
    Selectable<String, ColumnRow<Value>>,
    Valuable<Map<String, Value>> {

    private final LinkedHashMap<String, ColumnRow<Value>> rows;
    private final int displayHeight;
    private final boolean evenColumnWidth;
    private final boolean evenOdd;
    private final boolean scrollable;
    private final boolean selectable;
    private final boolean multiselect;
    private final boolean highlight;
    private final boolean displaySearch;
    private final int rowPadding;
    private final int columnMargin;

    private final ScrollRows scrollRows;
    @Nullable
    private final Map<String, Button> headerButtons;
    private final List<Integer> maxColumnWidths;
    @Nullable
    private StringInputSprite search;
    private int evenOddCounter;

    @Builder
    public Table(
        Map<String, ColumnRow<Value>> rows,
        int displayHeight,
        @Nullable Boolean evenColumnWidth,
        @Nullable Boolean evenOdd,
        @Nullable Boolean scrollable,
        @Nullable Boolean selectable,
        @Nullable Boolean multiselect,
        @Nullable Boolean highlight,
        @Nullable Boolean displaySearch,
        int rowPadding,
        int columnMargin,
        @Nullable COLOR backgroundColor,
        @Nullable Map<String, Button> headerButtons,
        @Nullable StringInputSprite search
    ) {
        super((backgroundColor != null) ? backgroundColor : COLOR.WHITE20);
        assert (!rows.isEmpty()) : "rows must not be empty";
        this.rows = new LinkedHashMap<>(rows);
        this.displayHeight = (displayHeight > 0) ? displayHeight : 150;
        this.scrollable = (scrollable != null) ? scrollable : false;
        this.evenOdd = (evenOdd != null) ? evenOdd : true;
        this.evenColumnWidth = (evenColumnWidth != null) ? evenColumnWidth : false;
        this.multiselect = (multiselect != null) ? multiselect : false;
        this.selectable = (selectable != null) ? selectable : false;
        this.highlight = (highlight != null) ? highlight : false;
        this.displaySearch = (displaySearch != null) ? displaySearch : false;
        this.headerButtons = headerButtons;
        this.search = search;
        this.rowPadding = rowPadding;
        this.columnMargin = columnMargin;


        // max width and margin for each column
        maxColumnWidths = UiUtil.getMaxColumnWidths(rows.values());

        // prepare header if present
        if (headerButtons != null) {
            List<Integer> headerButtonWidths = UiUtil.getWidths(headerButtons.values());

            // adjust max width when header columns are wider
            for (int i = 0, headerButtonWidthsSize = headerButtonWidths.size(); i < headerButtonWidthsSize; i++) {
                Integer buttonWith = headerButtonWidths.get(i);

                if (i < maxColumnWidths.size() && buttonWith > maxColumnWidths.get(i)) {
                    maxColumnWidths.set(i, buttonWith);
                }
            }

            headerButtons.values().forEach(button -> button.bg(COLOR.WHITE15));
        }

        // make every column the same with?
        if (this.evenColumnWidth) {
            Integer maxWidth = maxColumnWidths.stream().max(Comparator.naturalOrder())
                .orElse(null);

            if (maxWidth != null) {
                Integer[] newMaxWidths = new Integer[maxColumnWidths.size()];
                Arrays.fill(newMaxWidths, maxWidth);

                maxColumnWidths.clear();
                maxColumnWidths.addAll(Arrays.asList(newMaxWidths));
            }
        }

        // initialize rows
        for (Map.Entry<String, ColumnRow<Value>> entry : this.rows.entrySet()) {
            ColumnRow<Value> columnRow = entry.getValue();
            prepareColumnRow(columnRow);
        }

        // add header if needed
        if (headerButtons != null) {
            List<Integer> buttonMaxWidths = maxColumnWidths;
            // compensate button width with padding
            if (rowPadding > 0) {
                buttonMaxWidths = maxColumnWidths.stream().map(maxWidth -> {
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

        int searchBarMargin = 0;
        int searchBarHeight = 0;
        if (this.displaySearch) {
            if (this.search == null) {
                this.search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
            }
            Input searchField = new Input(this.search);
            addDown(0, searchField);
            searchBarMargin = 10;
            searchBarHeight = searchField.body().height();
        }

        // add the rows and scrollbar if needed
        scrollRows = ScrollRows.builder()
            .height(this.displayHeight - searchBarHeight - searchBarMargin)
            .rows(this.rows.values())
            .slide(this.scrollable)
            .search(this.search)
            .build();

        addDown(searchBarMargin, scrollRows.view());
    }

    private void prepareColumnRow(ColumnRow<Value> columnRow) {
        columnRow.margin(columnMargin);
        columnRow.init(maxColumnWidths);
        columnRow.pad(rowPadding);
        columnRow.selectable(selectable);
        if (!columnRow.isHeader()) columnRow.highlightable(highlight);

        // unselect other rows on click?
        if (selectable && !multiselect) {
            columnRow.clickAction(() -> {
                this.rows.values().stream()
                    .filter(row -> !row.equals(columnRow))
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

    public void display(Value value, boolean display) {
        rows.forEach((s, row) -> {
            if (value.equals(row.getValue())) {
                row.visableSet(display);
            }
        });
    }

    public void display(String key, boolean display) {
        rows.computeIfPresent(key, (s, row) -> {
            row.visableSet(display);
            return row;
        });
    }

    @Override
    public List<String> search(String term) {
        return rows.values().stream()
            .filter(columnRow -> columnRow.search(term))
            .map(ColumnRow::searchTerm)
            .collect(Collectors.toList());
    }

    @Override
    public void select(List<String> keys) {
        for (String index : keys) {
            ColumnRow<Value> row = rows.get(index);

            if (row != null) {
                row.selectedSet(true);
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

    public void clickAction(VoidAction clickAction) {
        rows.forEach((key, row) -> row.clickAction(clickAction));
    }

    @Override
    public Map<String, @Nullable Value> getValue() {
        LinkedHashMap<String, Value> map = new LinkedHashMap<>();
        for (Map.Entry<String, ColumnRow<Value>> entry : rows.entrySet()) {
            if (!entry.getValue().isHeader()) {
                map.put(entry.getKey(), entry.getValue().getValue());
            }
        }

        return map;
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

    @Nullable
    public ColumnRow<Value> addRow(ColumnRow<Value> row) {
        String key = row.getKey();
        if (key == null) {
            key = String.valueOf(row.hashCode());
        }

        ColumnRow<Value> result = rows.put(key, row);
        if (result == null) {
            prepareColumnRow(row);
            scrollRows.addRow(row);
        }

        return result;
    }

    @Nullable
    public ColumnRow<Value> removeRow(ColumnRow<Value> row) {
        String key = row.getKey();
        if (key == null) {
            key = rows.entrySet().stream()
                .filter(stringColumnRowEntry -> stringColumnRowEntry.getValue().equals(row))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(String.valueOf(row.hashCode()));
        }

        ColumnRow<Value> result = rows.remove(key);
        if (result != null) {
            evenOddCounter--;
            scrollRows.removeRow(row);
        }

        return result;
    }

    public void clearRows() {
        rows.clear();
        scrollRows.clearRows();
    }

    public static class TableBuilder<Value> {

        private Map<String, ColumnRow<Value>> rows = new LinkedHashMap<>();

        public TableBuilder<Value> category(String title, List<ColumnRow<Value>> rows) {
            GHeader header = new GHeader(title, UI.FONT().H2);
            GuiSection headerSection = UiUtil.toGuiSection(header);
            headerSection.pad(10);
            ColumnRow<Value> headerRow = ColumnRow.<Value>builder()
                .columns(Lists.of(headerSection))
                .build();
            headerRow.isHeader(true);
            add(headerRow);
            rows.forEach(this::add);

            return this;
        }

        public TableBuilder<Value> rowsCategorized(Map<String, List<ColumnRow<Value>>> rowsCategorized) {
            rowsCategorized.forEach(this::category);
            return this;
        }

        public TableBuilder<Value> rowsWithColumns(List<List<GuiSection>> rows) {
            rows.stream()
                .map(guiSections -> ColumnRow.<Value>builder()
                    .columns(guiSections)
                    .build())
                .forEach(this::add);

            return this;
        }

        public TableBuilder<Value> rows(List<ColumnRow<Value>> rows) {
            rows.forEach(this::add);
            return this;
        }

        public TableBuilder<Value> row(ColumnRow<Value> row) {
            add(row);
            return this;
        }

        private void add(ColumnRow<Value> row) {
            String key = row.getKey();
            if (key == null) {
                key = String.valueOf(row.hashCode());
            }

            rows.put(key, row);
        }
    }
}
