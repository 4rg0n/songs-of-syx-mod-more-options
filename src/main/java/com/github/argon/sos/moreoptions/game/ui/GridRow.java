package com.github.argon.sos.moreoptions.game.ui;

import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * Represents a row with multiple columns.
 * Each column has the same width.
 */
public class GridRow extends GuiSection {

    private COLOR color;
    @Getter
    private final List<GuiSection> columns;

    public GridRow(List<GuiSection> columns) {
        this.columns = columns;

        pad(5, 5);
    }

    public void initGrid(int columnWidth, int columnHeight) {
        clear();
        for (int i = 0; i < columns.size(); i++) {
            GuiSection section = columns.get(i);
            addGridD(section, i, columns.size(), columnWidth, columnHeight, DIR.W);
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
