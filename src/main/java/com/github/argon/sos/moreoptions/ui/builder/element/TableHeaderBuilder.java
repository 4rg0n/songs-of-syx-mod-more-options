package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.game.ui.Spacer;
import com.github.argon.sos.moreoptions.ui.builder.BuildResults;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilderList;
import com.github.argon.sos.moreoptions.util.UiUtil;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
public class TableHeaderBuilder implements UiBuilderList<RENDEROBJ, RENDEROBJ> {

    private final List<List<? extends GuiSection>> rows;

    private final int displayHeight;

    private final boolean evenOdd;
    private final int widthTotal;
    private final String key;
    private final Map<String, List<List<? extends GuiSection>>> mapRows;

    public BuildResults<RENDEROBJ, RENDEROBJ> build() {
        List<RENDEROBJ> renderobjs = new LinkedList<>();

        GHeader header = new GHeader(key, UI.FONT().H2);
        header.hoverInfoSet(key);

        Spacer spacerTop = new Spacer();
        spacerTop.body().setHeight(20);
        Spacer spacerBottom = new Spacer();
        spacerBottom.body().setHeight(10);

        renderobjs.add(spacerTop);
        renderobjs.add(header);
        renderobjs.add(spacerBottom);

        List<List<? extends GuiSection>> innerRows = mapRows.get(key);
        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(innerRows);

        List<GridRow> gridRows = innerRows.stream()
                .map(columns -> {
                    GridRow gridRow = new GridRow(columns);
                    gridRow.initGrid(maxWidths, widthTotal);

                    if (innerRows.indexOf(columns) % 2 == 0) {
                        gridRow.background(COLOR.WHITE15);
                    }

                    return gridRow;
                })
                .collect(Collectors.toList());

        renderobjs.addAll(gridRows);


        return BuildResults.<RENDEROBJ, RENDEROBJ>builder()
                .elements(renderobjs)
                .results(renderobjs)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private List<List<? extends GuiSection>> rows;

        @lombok.Setter
        @Accessors(fluent = true)
        private int displayHeight;

        @lombok.Setter
        @Accessors(fluent = true)
        private boolean evenOdd = false;

        @lombok.Setter
        @Accessors(fluent = true)
        private int widthTotal;

        @lombok.Setter
        @Accessors(fluent = true)
        private String key;

        @lombok.Setter
        @Accessors(fluent = true)
        Map<String, List<List<? extends GuiSection>>> mapRows;

        public BuildResults<RENDEROBJ, RENDEROBJ> build() {
            assert rows != null : "rows must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";
            assert widthTotal > 0 : "widthTotal must be greater than 0";
            assert key != null : "key must not be null";

            return new TableHeaderBuilder(rows, displayHeight, evenOdd, widthTotal, key, mapRows).build();
        }
    }
}
