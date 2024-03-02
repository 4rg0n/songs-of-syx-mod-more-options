package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
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

/**
 * Container, which aligns elements evenly in the row
 */
public class ColumnRow extends GuiSection implements Searchable<String, Boolean> {

    @Nullable
    @Setter
    @Accessors(fluent = true)
    private COLOR backgroundColor;

    @Setter
    @Accessors(fluent = true)
    private COLOR hoverColor = COLOR.WHITE35;

    @Getter
    private final List<GuiSection> columns;

    @Nullable
    @Setter
    @Getter
    @Accessors(fluent = true)
    private String searchTerm;

    @Setter
    @Accessors(fluent = true)
    private boolean highlight;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean isHeader = false;

    public ColumnRow(List<GuiSection> columns) {
        this(columns, null, false);
    }

    public ColumnRow(List<GuiSection> columns, @Nullable String searchTerm, boolean highlight) {
        this.columns = new ArrayList<>(columns);
        this.searchTerm = searchTerm;
        this.highlight = highlight;

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
        if (hoveredIs() && highlight) {
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
    public boolean hover(COORDINATE mCoo) {
        return super.hover(mCoo);
    }
}
