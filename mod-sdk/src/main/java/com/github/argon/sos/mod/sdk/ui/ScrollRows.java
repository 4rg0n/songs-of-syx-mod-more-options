package com.github.argon.sos.mod.sdk.ui;

import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;


public class ScrollRows extends GScrollRows {

    @Nullable
    private final StringInputSprite search;


    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Function<String, Boolean> searchAction;

    @Builder
    public ScrollRows(
        Collection<RENDEROBJ> rows,
        int height,
        int width,
        boolean slide,
        @Nullable StringInputSprite search
    ) {
        super(rows, height, width, slide);
        this.search = search;
    }

    @Override
    protected boolean passesFilter(int i, RENDEROBJ o) {
        if (!o.visableIs()) {
            return false;
        }

        if (search == null) {
            return true;
        }
        if (search.text().length() == 0) {
            return true;
        }

        if (searchAction != null) {
            return searchAction.apply(search.text().toString());
        }

        if (o instanceof ColumnRow) {
            ColumnRow<?> columnRow = (ColumnRow<?>) o;
            return columnRow.search(search.text().toString());
        } else if (o instanceof Button) {
            Button button = (Button) o;
            return button.search(search.text().toString());
        }

        return false;
    }

    public static class ScrollRowsBuilder {
        private Collection<RENDEROBJ> rows;

        public ScrollRowsBuilder rows(Collection<? extends RENDEROBJ> rows) {
            this.rows = new LinkedList<>(rows);

            return this;
        }
    }
}
