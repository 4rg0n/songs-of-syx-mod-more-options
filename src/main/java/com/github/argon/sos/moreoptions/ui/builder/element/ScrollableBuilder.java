package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;
import snake2d.util.sets.LinkedList;
import util.gui.table.GScrollRows;

import java.util.List;

/**
 * Builds a scrollable list out of {@link GridRow}s
 */
@Builder
@RequiredArgsConstructor
public class ScrollableBuilder implements UiBuilder<GScrollRows, GScrollRows> {

    private final List<? extends GuiSection> rows;

    private final int height;

    public BuildResult<GScrollRows, GScrollRows> build() {
        LinkedList<GuiSection> rows = new LinkedList<>(this.rows);
        GScrollRows gScrollRows = new GScrollRows(rows, height);

        return BuildResult.<GScrollRows, GScrollRows>builder()
            .element(BuildResult.NO_KEY, gScrollRows)
            .result(gScrollRows)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private List<? extends GuiSection> rows;

        @lombok.Setter
        @Accessors(fluent = true)
        private int height;

        public ScrollableBuilder build() {
            assert rows != null : "rows must not be null";
            assert height > 0 : "height must be greater than 0";

            return new ScrollableBuilder(rows, height);
        }
    }
}
