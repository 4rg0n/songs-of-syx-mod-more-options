package com.github.argon.sos.moreoptions.game.ui;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sets.LinkedList;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.table.GScrollRows;

import java.util.List;


public class ScrollRows extends GScrollRows {

    @Nullable
    private StringInputSprite search;

    @Builder
    public ScrollRows(Iterable<RENDEROBJ> rows, int height, int width, boolean slide, StringInputSprite search) {
        super(rows, height, width, slide);
         if (search != null) this.search = search;
    }

    @Override
    protected boolean passesFilter(int i, RENDEROBJ o) {
        if (search == null) {
            return true;
        }
        if (search.text().length() == 0) {
            return true;
        }
        if (o instanceof ColumnRow) {
            ColumnRow<?> columnRow = (ColumnRow<?>) o;
            return columnRow.search(search.text().toString());
        }

        return false;
    }

    public static class ScrollRowsBuilder {
        private Iterable<RENDEROBJ> rows;

        public ScrollRowsBuilder rows(List<? extends RENDEROBJ> rows) {
            LinkedList<RENDEROBJ> rowsLinked = new LinkedList<>(rows);
            this.rows = rowsLinked;

            return this;
        }
    }
}
