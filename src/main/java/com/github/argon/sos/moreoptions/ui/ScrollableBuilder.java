package com.github.argon.sos.moreoptions.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sets.LinkedList;
import util.gui.table.GScrollRows;

import java.util.List;

/**
 * Builds a scrollable list out of {@link GridRow}s
 */
public class ScrollableBuilder {

    public GScrollRows build(LinkedList<GridRow> columnRows, int height) {
        return new GScrollRows(columnRows, height);
    }

    /**
     * Represents a row with multiple columns.
     * Each column has the same width.
     */
    public static class GridRow extends GuiSection {

        private COLOR color;

        public GridRow(List<GuiSection> columns, int rowHeight, int width) {
            for (int i = 0; i < columns.size(); i++) {
                GuiSection r = columns.get(i);
                addGridD(r, i, columns.size(), width, rowHeight, DIR.W);
            }
            pad(5, 5);
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
}
