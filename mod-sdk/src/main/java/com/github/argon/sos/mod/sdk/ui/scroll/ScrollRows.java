package com.github.argon.sos.mod.sdk.ui.scroll;

import com.github.argon.sos.mod.sdk.ui.button.Button;
import com.github.argon.sos.mod.sdk.ui.table.ColumnRow;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * For making content scroll vertically.
 */
public class ScrollRows extends AbstractScrollRows {

    @Nullable
    private final StringInputSprite search;


    @Setter
    @Nullable
    @Accessors(fluent = true, chain = false)
    private Function<String, Boolean> searchAction;

    /**
     * Creates new {@link ScrollRows}.
     *
     * @param rows to add
     * @param height available height. If rows exceed the height, the scrollbar will be added.
     * @param width available width
     * @param slide whether to always show the scrollbar
     * @param search optional search input for filtering rows
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean passesFilter(int position, RENDEROBJ row) {
        if (!row.visableIs()) {
            return false;
        }

        if (search == null) {
            return true;
        }
        if (search.text().isEmpty()) {
            return true;
        }

        if (searchAction != null) {
            return searchAction.apply(search.text().toString());
        }

        if (row instanceof ColumnRow<?> columnRow) {
            return columnRow.search(search.text().toString());
        } else if (row instanceof Button) {
            Button<String> button = (Button<String>) row;
            return button.search(search.text().toString());
        }

        return false;
    }

    /**
     * Overridden lombok builder with extra methods.
     */
    public static class ScrollRowsBuilder {
        private Collection<RENDEROBJ> rows;

        public ScrollRowsBuilder rows(Collection<? extends RENDEROBJ> rows) {
            this.rows = new LinkedList<>(rows);

            return this;
        }
    }
}
