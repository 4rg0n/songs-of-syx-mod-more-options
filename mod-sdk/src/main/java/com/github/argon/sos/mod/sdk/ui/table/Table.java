package com.github.argon.sos.mod.sdk.ui.table;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.ui.simple.ColorBox;
import com.github.argon.sos.mod.sdk.ui.button.AbstractButton;
import com.github.argon.sos.mod.sdk.ui.menu.ButtonMenu;
import com.github.argon.sos.mod.sdk.ui.input.InputString;
import com.github.argon.sos.mod.sdk.ui.scroll.ScrollRows;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GHeader;

import java.util.*;

/**
 * For displaying content in a table format.
 *
 * @param <Value> type of the value(s) contained in the table
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
    private final Map<String, ? extends AbstractButton<String>> headerButtons;
    private final List<Integer> maxColumnWidths;
    @Nullable
    private StringInputSprite search;
    private int evenOddCounter;

    /**
     * Builds a table with rows and columns.
     *
     * @param rows key value map containing the rows to display
     * @param displayHeight max available height for displaying
     * @param evenColumnWidth whether each column shall get the same size; determined by the widest
     * @param evenOdd whether each second row shall get another color
     * @param scrollable whether the table shall display a scrollbar
     * @param selectable whether rows in this table shall be selectable
     * @param multiselect whether selecting multiple rows is allowed
     * @param highlight whether a row shall get another color when hovered with a mouse
     * @param displaySearch whether a default search bar shall be displayed
     * @param rowPadding used as framed space around each row in pixel
     * @param columnMargin space between each row
     * @param backgroundColor of each row
     * @param headerButtons list of buttons used as header for columns
     * @param search optional custom search bar
     */
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
        @Nullable Map<String, AbstractButton<String>> headerButtons,
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
                int buttonWith = headerButtonWidths.get(i) + columnMargin;

                if (rowPadding > 0) {
                    buttonWith += (int) Math.ceil((double) (rowPadding * 2) / headerButtonWidths.size());
                }

                if (buttonWith > maxColumnWidths.get(i)) {
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
            ButtonMenu<String> header = ButtonMenu.<String>builder()
                .buttons(headerButtons)
                .horizontal(true)
                .notClickable(true)
                .notHoverable(true)
                .widths(maxColumnWidths)
                .build();

            addDown(0, header);
        }

        int searchBarMargin = 0;
        int searchBarHeight = 0;
        if (this.displaySearch) {
            if (this.search == null) {
                this.search = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
            }
            InputString searchField = new InputString(this.search);
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

        addDown(searchBarMargin, scrollRows.getView());
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

    /**
     * Shows or hides rows based on its value.
     *
     * @param value of the rows to hide or display
     * @param display whether rows shall be displayed or not
     */
    public void display(Value value, boolean display) {
        rows.forEach((s, row) -> {
            if (value.equals(row.getValue())) {
                row.visableSet(display);
            }
        });
    }

    /**
     * Shows or hides a row by given key.
     *
     * @param key of the row to hide or display
     * @param display whether row shall be displayed or not
     */
    public void display(String key, boolean display) {
        rows.computeIfPresent(key, (s, row) -> {
            row.visableSet(display);
            return row;
        });
    }

    /**
     * Returns the search results for the given term.
     *
     * @param term to search for
     * @return search results for the given term
     */
    @Override
    public List<String> search(String term) {
        return searchRows(term).stream()
            .map(ColumnRow::searchTerm)
            .toList();
    }

    /**
     * Returns a list of rows matching the search term.
     *
     * @param term to search for
     * @return rows matching the search term
     */
    public List<ColumnRow<Value>> searchRows(String term) {
        return rows.values().stream()
            .filter(columnRow -> columnRow.search(term))
            .toList();
    }

    /**
     * Selects the rows with the given keys.
     *
     * @param keys to select
     */
    @Override
    public void select(List<String> keys) {
        for (String index : keys) {
            ColumnRow<Value> row = rows.get(index);

            if (row != null) {
                row.selectedSet(true);
            }
        }
    }

    /**
     * Returns selected rows.
     *
     * @return selected rows
     */
    @Override
    public List<ColumnRow<Value>> getSelection() {
        return rows.values().stream()
            .filter(GuiSection::selectedIs)
            .toList();
    }

    /**
     * Sets a given {@link Action} when double-clicking to each row.
     *
     * @param doubleClickAction to set
     */
    public void doubleClickAction(Action<ColumnRow<Value>> doubleClickAction) {
        rows.forEach((key, row) -> row.doubleClickAction(doubleClickAction));
    }

    /**
     * Sets a given {@link Action} when clicking to each row.
     *
     * @param clickAction to set
     */
    public void clickAction(VoidAction clickAction) {
        rows.forEach((key, row) -> row.clickAction(clickAction));
    }

    /**
     * Returns a map with row keys and their values.
     *
     * @return map with row keys and their values
     */
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

    /**
     * Sets a map with row keys and their values.
     *
     * @param values to set
     */
    @Override
    public void setValue(Map<String, Value> values) {
        values.forEach((key, value) -> {
            ColumnRow<Value> row = rows.get(key);

            if (row != null) {
                row.setValue(value);
            }
        });
    }

    /**
     * Adds a row at the end or replaces a row with the same {@code ColumnRow.getKey()} in the table.
     *
     * @param row to add
     * @return added row
     */
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

    /**
     * Removes a row from the table.
     *
     * @param row to remove
     * @return removed row or null when there was no row to remove
     */
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

    /**
     * Clears the table and removes all rows.
     */
    public void clearRows() {
        rows.clear();
        scrollRows.clearRows();
    }

    /**
     * Overridden lombok builder with extra methods.
     *
     * @param <Value> type of the values contained in the table
     */
    public static class TableBuilder<Value> {

        private final Map<String, ColumnRow<Value>> rows = new LinkedHashMap<>();

        /**
         * Adds a category row to the table.
         *
         * @param title of the category
         * @param rows for the category
         * @return this
         */
        public TableBuilder<Value> category(String title, List<ColumnRow<Value>> rows) {
            GHeader header = new GHeader(title, UI.FONT().H2);
            GuiSection headerSection = UiMapper.toGuiSection(header);
            headerSection.pad(10);
            ColumnRow<Value> headerRow = ColumnRow.<Value>builder()
                .columnsFromRenderObjects(List.of(headerSection))
                .build();
            headerRow.isHeader(true);
            add(headerRow);
            rows.forEach(this::add);

            return this;
        }

        /**
         * Adds a map with category names and their rows to the table.
         *
         * @param rowsCategorized to add
         * @return this
         */
        public TableBuilder<Value> rowsCategorized(Map<String, List<ColumnRow<Value>>> rowsCategorized) {
            rowsCategorized.forEach(this::category);
            return this;
        }

        /**
         * Adds a two-dimensional list with {@link GuiSection}s as rows and columns.
         *
         * @param grid rows containing columns of ui elements
         * @return this
         */
        public TableBuilder<Value> rowsWithColumns(List<List<GuiSection>> grid) {
            grid.stream()
                .map(guiSections -> ColumnRow.<Value>builder()
                    .columnsFromRenderObjects(guiSections)
                    .build())
                .forEach(this::add);

            return this;
        }

        /**
         * Adds a list of {@link ColumnRow}s to the table.
         *
         * @param rows to add
         * @return this
         */
        public TableBuilder<Value> rows(List<ColumnRow<Value>> rows) {
            rows.forEach(this::add);
            return this;
        }

        /**
         * Adds a {@link ColumnRow}s to the table.
         *
         * @param row to add
         * @return this
         */
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
