package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Container, which aligns elements evenly in the row.
 * Used by the {@link Table} element.
 */
public class ColumnRow<Value> extends GuiSection implements
    Searchable<String, Boolean>,
    Valuable<Value, ColumnRow<Value>> {

    @Nullable
    @Setter
    @Accessors(fluent = true)
    private COLOR backgroundColor;

    @Setter
    @Accessors(fluent = true)
    private COLOR hoverColor = COLOR.WHITE35;

    @Setter
    @Accessors(fluent = true)
    private COLOR selectionColor = COLOR.BLUEISH;

    @Getter
    private final List<GuiSection> columns;

    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String searchTerm;

    @Setter
    @Accessors(fluent = true)
    private boolean highlightable;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean isHeader;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean selectable;

    private float doubleClickTimer = 0;

    @Setter
    private Value value;
    private boolean isSelected;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Supplier<Value> valueSupplier;

    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Action<ColumnRow<Value>> doubleClickAction;

    @Setter
    @Accessors(fluent = true, chain = false)
    private Action<ColumnRow<Value>> clickAction = o -> {};


    public ColumnRow(List<GuiSection> columns) {
        this(columns, null, false, false);
    }

    @Builder
    public ColumnRow(List<GuiSection> columns, @Nullable String searchTerm, boolean highlightable, boolean selectable) {
        this.columns = new ArrayList<>(columns);
        this.searchTerm = searchTerm;
        this.highlightable = highlightable;
        this.selectable = selectable;

        pad(5, 5);
    }

    public void init() {
        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(Lists.of(columns));
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
            addRightC(5, section);
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
        clickAction.accept(this);
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

    public Value getValue() {
        if (valueSupplier != null) {
            return valueSupplier.get();
        }

        return value;
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
}
