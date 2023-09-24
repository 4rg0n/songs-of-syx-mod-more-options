package com.github.argon.sos.moreoptions.ui;

import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sets.LinkedList;
import util.gui.table.GScrollRows;

import java.util.List;

public class ScrollableBuilder {

    public GScrollRows build(LinkedList<ColumnRow> columnRows, int height) {
        return new GScrollRows(columnRows, height);
    }

    public static class ColumnRow extends GuiSection {

        public ColumnRow(List<GuiSection> columns, int width, int height) {
            for (int i = 0; i < columns.size(); i++) {
                GuiSection r = columns.get(i);
                addGridD(r, i, columns.size(), width, height, DIR.W);
            }
            pad(5, 5);
        }
    }
}
