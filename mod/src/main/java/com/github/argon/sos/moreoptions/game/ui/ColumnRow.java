package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.mod.sdk.game.action.*;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Container, which aligns elements evenly in the row.
 * Used by the {@link Table} element.
 */
@Builder
public class ColumnRow<Value> extends Section implements
    Searchable<String, Boolean>,
    Valuable<Value>,
    Toggleable<String>
{
    /**
     * Used to identify the row
     */
    @Getter
    @Builder.Default
    private String key = UUID.randomUUID().toString();

    @Nullable
    @Setter
    @Accessors(fluent = true)
    private COLOR backgroundColor;

    /**
     * Color when moving the mouse over row
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private COLOR hoverColor = COLOR.WHITE35;

    /**
     * Color after clicking on the row
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private COLOR selectionColor = COLOR.BLUEISH;

    @Getter
    private final List<GuiSection> columns;

    /**
     * Used for searching through a list of rows e.g. in a {@link Table}
     */
    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String searchTerm;

    /**
     * Whether row shall be highlighted when moving mouse over it
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean highlightable = false;

    /**
     * Whether this row shall be treated as header.
     * The row will be ignored by the search e.g.
     */
    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean isHeader = false;

    /**
     * Whether this row is selectable by clicking with  the mouse on it
     */
    @Getter
    @Setter
    @Builder.Default
    @Accessors(fluent = true)
    private boolean selectable = false;

    /**
     * Space between in row in pixels
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
     * When ever something is setting a value into this row. This gets called.
     */
    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Consumer<Value> valueConsumer;

    /**
     * What shall happen when you doubleclick the row.
     */
    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<ColumnRow<Value>> doubleClickAction;

    /**
     * What shall happen when you do a single click on this row
     */
    @Setter
    @Builder.Default
    @Accessors(fluent = true, chain = false)
    private VoidAction clickAction = () -> {};

    public void init() {
        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(Lists.ofSingle(columns));
        init(maxWidths);
    }

    /**
     * Applies widths to each element in the row
     *
     * @param columnWidths to apply for each element
     */
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

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (doubleClickTimer >= 0) doubleClickTimer -= ds;

        if (selectedIs()) {
            selectionColor.render(r, body());
        } else if (highlightable && hoveredIs()) {
            hoverColor.render(r, body());
        } else if (backgroundColor != null) {
            backgroundColor.render(r, body());
        }

        super.render(r, ds);
    }

    @Override
    public Boolean search(String s) {
        if (searchTerm == null) {
            return true;
        }

        return searchTerm.toLowerCase().contains(s.toLowerCase());
    }

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

    @Override
    public boolean hover(COORDINATE mCoo) {
        return super.hover(mCoo);
    }

    @Override
    public Value getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return value;
    }

    @Override
    public void setValue(Value value) {
        if (valueConsumer != null) {
            valueConsumer.accept(value);
        }

        this.value = value;
    }

    @Override
    public ColumnRow<Value> selectedSet(boolean selected) {
        isSelected = selected;

        return this;
    }

    @Override
    public boolean selectedIs() {
        return isSelected;
    }

    @Override
    public ColumnRow<Value> selectedToggle() {
        isSelected = !isSelected;
        return this;
    }

    /**
     * @param index of column
     * @param clazz to cast to
     * @return found column cast to given class or null if there's no element at this index
     * @param <Element> type of the element in column
     *
     * @throws ClassCastException when the element could not be cast to given class
     */
    @Nullable
    public <Element> Element getColumnAs(int index, Class<Element> clazz) {
        // nothing there to get?
        if (index > columns.size() - 1) {
            return null;
        }

        //noinspection unchecked
        return (Element) clazz;
    }

    /**
     * @param clazz of element
     * @return found element
     * @param <Element> type of element you are looking for
     */
    public <Element> Optional<Element> findInColumns(Class<Element> clazz) {
        return columns.stream()
            .filter(column -> ClassUtil.instanceOf(column, clazz))
            .map(clazz::cast)
            .findFirst();
    }

    public static class ColumnRowBuilder<Value> {

        private final List<GuiSection> columns = new ArrayList<>();

        public ColumnRowBuilder<Value> columns(List<? extends RENDEROBJ> columns) {
            columns.forEach(this::column);
            return this;
        }

        public ColumnRowBuilder<Value> column(RENDEROBJ column) {
            columns.add(UiUtil.toGuiSection(column));
            return this;
        }
    }

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
