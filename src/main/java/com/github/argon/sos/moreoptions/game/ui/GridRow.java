package com.github.argon.sos.moreoptions.game.ui;

import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

import java.util.List;

public class GridRow extends GuiSection {

    private COLOR color;
    @Getter
    private final List<? extends GuiSection> columns;

    public GridRow(List<? extends GuiSection> columns) {
        this.columns = columns;

        pad(5, 5);
    }

    public void initGrid(List<Integer> columnWidths) {
        clear();

        if (columnWidths.size() != columns.size()) {
            throw new RuntimeException("Amount of columns (" + columns.size() + ") in row does match given column widths " + columnWidths.size());
        }

        for (int i = 0; i < columns.size(); i++) {
            GuiSection section = columns.get(i);
            Integer width = columnWidths.get(i);
            section.body().setWidth(width);
            addRight(5, section);
        }
    }

    public void initGrid(List<Integer> columnWidths, Integer width) {
        clear();

        int lastWidth = 0;
        for (int i = 1; i < columnWidths.size(); i++) {
            lastWidth += columnWidths.get(i);
        }

        columnWidths.set(0, width - lastWidth);

        for (int i = 0; i < columns.size(); i++) {
            GuiSection section = columns.get(i);
            Integer widthColumn = columnWidths.get(i);
            section.body().setWidth(widthColumn);
            addRight(5, section);
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
