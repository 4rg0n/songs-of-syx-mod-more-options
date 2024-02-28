package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Container, which aligns elements evenly in the row
 */
public class ColumnRow extends GuiSection implements Searchable<String, Boolean> {

    @Nullable
    private COLOR color;
    @Getter
    private final List<GuiSection> columns;

    @Nullable
    @Setter
    @Getter
    private String searchTerm;

    @Getter
    @Setter
    private boolean header = false;

    public ColumnRow(List<GuiSection> columns) {
        this(columns, null);
    }

    public ColumnRow(List<GuiSection> columns, @Nullable String searchTerm) {
        this.columns = new ArrayList<>(columns);
        this.searchTerm = searchTerm;

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
        if (color != null) {
            this.color.render(r, this.body());
        }
        super.render(r, ds);
    }

    public GuiSection background(COLOR c) {
        this.color = c;
        return this;
    }

    public GuiSection backgroundClear() {
        this.color = COLOR.WHITE50;
        return this;
    }

    @Override
    public Boolean search(String s) {
        if (searchTerm == null) {
            return true;
        }

        return searchTerm.toLowerCase().contains(s.toLowerCase());
    }
}
