package com.github.argon.sos.moreoptions.ui.builder;

import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Builder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BuildResult<Result, Element> {

    private Result result;

    private Element interactable;

    public <Value> BuildResult<ColumnRow<Value>, Element> toColumnRow() {

        // check for type List<? extends GuiSection> and build
        if (result instanceof List) {
            List<?> listResult = (List<?>) result;

            List<GuiSection> columns = listResult.stream()
                .map(UiUtil::toGuiSection)
                .collect(Collectors.toList());

            if (columns.isEmpty()) {
                throw new IllegalArgumentException("No viable ui elements to put into grid row found in result list.");
            }

            ColumnRow<Value> columnRow = ColumnRow.<Value>builder()
                .columns(columns)
                .build();
            columnRow.init();

            return BuildResult.<ColumnRow<Value>, Element>builder()
                .result(columnRow)
                .interactable(interactable)
                .build();
        }

        throw new IllegalArgumentException("The result must be from type List<? extends GuiSection>");
    }
}
