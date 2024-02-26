package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * Container, which aligns elements evenly in the row
 */
public class ColumnRow extends GuiSection {

    @Nullable
    private COLOR color;
    @Getter
    private final List<? extends GuiSection> columns;

    public ColumnRow(List<? extends GuiSection> columns) {
        this.columns = columns;

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

        if (columnWidths.size() != columns.size()) {
            throw new RuntimeException("Amount of columns (" + columns.size() + ") in row does match given column widths " + columnWidths.size());
        }

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
}