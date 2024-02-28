package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sets.LinkedList;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.table.GScrollRows;

import java.util.List;

/**
 * Builds a scrollable list out of {@link ColumnRow}s
 */
@Builder
@RequiredArgsConstructor
public class ScrollableBuilder implements UiBuilder<GScrollRows, GScrollRows> {

    private final List<? extends GuiSection> rows;

    private final int height;

    @Nullable
    private final StringInputSprite search;

    public BuildResult<GScrollRows, GScrollRows> build() {
        LinkedList<GuiSection> rows = new LinkedList<>(this.rows);

        GScrollRows gScrollRows;
        if (search == null) {
            gScrollRows = new GScrollRows(rows, height);
        } else {
            gScrollRows = new GScrollRows(rows, height) {

                @Override
                protected boolean passesFilter(int i, RENDEROBJ o) {
                    if (search.text().length() == 0)
                        return true;
                    if (o instanceof ColumnRow) {
                        ColumnRow columnRow = (ColumnRow) o;
                        return columnRow.search(search.text().toString());
                    } else {
                        return false;
                    }
                }
            };
        }

        return BuildResult.<GScrollRows, GScrollRows>builder()
            .interactable(gScrollRows)
            .result(gScrollRows)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private List<? extends GuiSection> rows;

        @Accessors(fluent = true)
        private int height;

        @Accessors(fluent = true)
        private StringInputSprite search;

        public BuildResult<GScrollRows, GScrollRows> build() {
            assert rows != null : "rows must not be null";
            assert height > 0 : "height must be greater than 0";

            return new ScrollableBuilder(rows, height, search).build();
        }
    }
}
