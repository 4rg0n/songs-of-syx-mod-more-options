package com.github.argon.sos.mod.sdk.ui.table;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.ui.util.Section;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Container, which aligns elements evenly in the row.
 * Used by the {@link Table} element.
 *
 * @param <Value> the type of the value contained in the column row
 */
@Builder
public class ColumnRow<Value> extends Section implements
    Searchable<String, Boolean>,
    Valuable<Value>,
    Toggleable<String>,
    Comparable<ColumnRow<Value>>,
    Initializable<List<Integer>>
{
    /**
     * Used to identify the row.
     */
    @Getter
    @Builder.Default
    private String key = UUID.randomUUID().toString();

    @Nullable
    @Setter
    @Accessors(fluent = true)
    private COLOR backgroundColor;

    /**
     * Color when moving the mouse over row.
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private COLOR hoverColor = COLOR.WHITE35;

    /**
     * Color after clicking on the row.
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private COLOR selectionColor = COLOR.BLUEISH;

    @Getter
    private List<GuiSection> columns;

    /**
     * Used for searching through a list of rows e.g. in a {@link Table}.
     */
    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String searchTerm;

    /**
     * Whether row shall be highlighted when moving mouse over it.
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean highlightable = false;

    /**
     * Whether this row shall be treated as header.
     * The row will be ignored by the search.
     */
    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean isHeader = false;

    /**
     * Whether this row is selectable by clicking with  the mouse on it.
     */
    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean selectable = false;

    /**
     * Space between in row in pixels.
     */
    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private int margin = 0;

    private float doubleClickTimer;

    /**
     * Possible value for this row.
     */
    @Nullable
    private Value value;
    private boolean isSelected;

    /**
     * A place where this row could read their value from.
     */
    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<Value> valueSupplier;

    /**
     * Whenever something is setting a value into this row. This gets called.
     */
    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Consumer<Value> valueConsumer;

    /**
     * For sorting rows in a list via their values by a defined behavior.
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true, chain = false)
    private Comparator<Value> comparator = (value1, value2) -> 0;

    /**
     * What shall happen when you doubleclick the row.
     */
    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<ColumnRow<Value>> doubleClickAction;

    /**
     * What shall happen when you do a single click on this row.
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true, chain = false)
    private VoidAction clickAction = () -> {};

    /**
     * Calculates the column width and initializes the row.
     */
    @Override
    public void init() {
        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(List.of(columns));
        init(maxWidths);
    }

    /**
     * Applies widths to each element in the row.
     *
     * @param columnWidths to apply for each element
     */
    @Override
    public void init(List<Integer> columnWidths) {
        clear();

        // edge case when there are rows with different column sizes
        if (columnWidths.size() < columns.size()) {
            int size = columns.size();
            // fill rest with their size
            for (int i = columnWidths.size(); i < size; i++) {
                columnWidths.add(columns.get(i).body().width());
            }
        } else if (columnWidths.size() > columns.size()) {
            int size = columnWidths.size();
            for (int i = columns.size(); i < size; i++) {
                GuiSection dummy = new GuiSection();
                dummy.body().setWidth(columnWidths.get(i));
                columns.add(dummy);
            }
        }

        initColumns(columnWidths);
    }

    private void initColumns(List<Integer> columnWidths) {
        for (int i = 0; i < columns.size(); i++) {
            GuiSection section = columns.get(i);
            Integer width = columnWidths.get(i);
            section.body().setWidth(width);
            addRightC(margin, section);
        }
    }

    /**
     * Execute when the {@link ColumnRow} is rendered.
     *
     * @param renderer to use
     * @param deltaSeconds since last render loop
     */
    @Override
    public void render(SPRITE_RENDERER renderer, float deltaSeconds) {
        if (doubleClickTimer >= 0) doubleClickTimer -= deltaSeconds;

        if (selectedIs()) {
            selectionColor.render(renderer, body());
        } else if (highlightable && hoveredIs()) {
            hoverColor.render(renderer, body());
        } else if (backgroundColor != null) {
            backgroundColor.render(renderer, body());
        }

        super.render(renderer, deltaSeconds);
    }

    /**
     * Searches for a {@link String} value in this row.
     * Uses lowercased contains.
     *
     * @param term to search for
     * @return whether row matches
     */
    @Override
    public Boolean search(String term) {
        if (searchTerm == null) {
            return true;
        }

        return searchTerm.toLowerCase().contains(term.toLowerCase());
    }

    /**
     * Executed when the {@link ColumnRow} is clicked.
     *
     * @return whether column row is clickable
     */
    @Override
    public boolean click() {
        if (!activeIs() || !visableIs())
            return false;
        clickAction.accept();
        if (selectable) selectedToggle();

        // no double click detection needed?
        if (doubleClickAction == null) {
            return super.click();
        } else {
            if (doubleClickTimer > 0) {
                doubleClickTimer = 0;
                // double click detected
                doubleClickAction.accept(this);
            } else {
                // doubleclick speed in seconds
                doubleClickTimer = 0.3f;
            }

            return false;
        }
    }

    /**
     * Executed when the {@link ColumnRow} is hovered with the mouse.
     *
     * @param mouseCoordinates x and y of the mouse
     * @return whether column row is hover-able
     */
    @Override
    public boolean hover(COORDINATE mouseCoordinates) {
        return super.hover(mouseCoordinates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Value value) {
        if (valueConsumer != null) {
            valueConsumer.accept(value);
        }

        this.value = value;
    }

    /**
     * Sets the selected state.
     *
     * @param selected whether column row is selected or not
     * @return this
     */
    @Override
    public ColumnRow<Value> selectedSet(boolean selected) {
        isSelected = selected;

        return this;
    }

    /**
     * Tells whether the {@link ColumnRow} is selected or not.
     *
     * @return whether column row is selected or not
     */
    @Override
    public boolean selectedIs() {
        return isSelected;
    }

    /**
     * Toggles the selected state on or off.
     *
     * @return this
     */
    @Override
    public ColumnRow<Value> selectedToggle() {
        isSelected = !isSelected;
        return this;
    }

    /**
     * Overridden lombok builder with extra methods.
     *
     * @param <Value> the type of the value contained in the column row
     */
    public static class ColumnRowBuilder<Value> {

        private List<GuiSection> columns = new ArrayList<>();

        /**
         * Adds new columns from a list of {@link RENDEROBJ}s.
         *
         * @param columns to add
         * @return this
         */
        public ColumnRowBuilder<Value> columnsFromRenderObjects(List<? extends RENDEROBJ> columns) {
            columns.forEach(this::column);
            return this;
        }

        /**
         * Adds a new column from a {@link RENDEROBJ}.
         * Will wrap the {@link RENDEROBJ} into a {@link GuiSection}
         *
         * @param column to add
         * @return this
         */
        public ColumnRowBuilder<Value> column(RENDEROBJ column) {
            columns.add(UiMapper.toGuiSection(column));
            return this;
        }
    }

    /**
     * Compares a {@link ColumnRow} instance with this one.
     *
     * @param row the column row to be compared.
     * @return whether they are equal or not
     */
    @Override
    public int compareTo(@NotNull ColumnRow<Value> row) {
        return comparator.compare(this.value, row.getValue());
    }

    /**
     * Transforms the {@link ColumnRow} into a {@link String}.
     *
     * @return transformed string
     */
    @Override
    public String toString() {
        return "ColumnRow{" +
            "key='" + key + '\'' +
            ", searchTerm='" + searchTerm + '\'' +
            ", highlightable=" + highlightable +
            ", isHeader=" + isHeader +
            ", selectable=" + selectable +
            ", visible=" + visableIs() +
            '}';
    }
}
