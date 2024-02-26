package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Spacer;
import com.github.argon.sos.moreoptions.ui.builder.BuildResults;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilderList;
import com.github.argon.sos.moreoptions.util.UiUtil;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

    private final Map<String, List<List<? extends GuiSection>>> headerWithRows;
    private final int displayHeight;
    private final boolean evenOdd;
    private final String key;


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

        List<List<? extends GuiSection>> innerRows = headerWithRows.get(key);
        List<Integer> maxWidths = UiUtil.getMaxColumnWidths(headerWithRows);

        List<ColumnRow> columnRows = innerRows.stream()
                .map(columns -> {
                    ColumnRow columnRow = new ColumnRow(columns);
                    columnRow.init(maxWidths);

                    if (innerRows.indexOf(columns) % 2 == 0) {
                        columnRow.background(COLOR.WHITE15);
                    }

                    return columnRow;
                })
                .collect(Collectors.toList());
        renderobjs.addAll(columnRows);

        return BuildResults.<RENDEROBJ, RENDEROBJ>builder()
                .elements(renderobjs)
                .results(renderobjs)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private int displayHeight;

        @Accessors(fluent = true)
        private boolean evenOdd = false;

        @Accessors(fluent = true)
        private String key;

        @Accessors(fluent = true)
        Map<String, List<List<? extends GuiSection>>> headerWithRows;

        public BuildResults<RENDEROBJ, RENDEROBJ> build() {
            assert headerWithRows != null : "headerWithRows must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";
            assert key != null : "key must not be null";

            return new TableHeaderBuilder(headerWithRows, displayHeight, evenOdd, key).build();
        }
    }
}
