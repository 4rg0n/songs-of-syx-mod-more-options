package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds a scrollable list out of {@link ColumnRow}s
 */
@Builder
@RequiredArgsConstructor
public class TableBuilder implements UiBuilder<Table, Table> {

    private final List<ColumnRow> rows;
    private final int displayHeight;
    private final boolean scrollable;
    private final boolean evenOdd;
    private final boolean evenColumnWidth;
    @Nullable
    private final StringInputSprite search;

    public BuildResult<Table, Table> build() {
        Table table = new Table(rows, displayHeight, scrollable, evenOdd, evenColumnWidth, search);
        return BuildResult.<Table, Table>builder()
            .interactable(table)
            .result(table)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        private List<ColumnRow> rows;

        @Accessors(fluent = true)
        private int displayHeight;

        @Accessors(fluent = true)
        private boolean scrollable = false;

        @Accessors(fluent = true)
        private boolean evenOdd = false;

        @Accessors(fluent = true)
        private boolean evenColumnWidth = false;

        @Accessors(fluent = true)
        private StringInputSprite search;

        public Builder columnRows(List<ColumnRow> rows) {
            this.rows = rows;
            return this;
        }

        public Builder rows(List<List<? extends GuiSection>> rows) {
            this.rows = rows.stream()
                .map(ColumnRow::new)
                .collect(Collectors.toList());

            return this;
        }

        public BuildResult<Table, Table> build() {
            assert rows != null : "rows must not be null";

            return new TableBuilder(rows, displayHeight, scrollable, evenOdd, evenColumnWidth, search).build();
        }
    }
}
