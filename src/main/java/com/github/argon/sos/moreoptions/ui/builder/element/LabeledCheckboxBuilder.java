package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class LabeledCheckboxBuilder implements UiBuilder<GridRow, Checkbox> {
    private final Definition definition;

    @Override
    public BuildResult<GridRow, Checkbox> build() {
        GuiSection label = LabelBuilder.builder().translate(definition.getLabelDefinition()).build().build().getResult();
        label.pad(10, 5);

        Checkbox checkbox = CheckboxBuilder.builder().definition(definition.getCheckboxDefinition()).build().build().getResult();
        GuiSection checkBoxSection = new GuiSection().add(checkbox);
        checkBoxSection.pad(10, 5);

        List<GuiSection> row = Stream.of(
            label,
            checkBoxSection
        ).collect(Collectors.toList());

        int maxHeight = UiUtil.getMaxHeight(row);
        int maxWidth = UiUtil.getMaxWidth(row);

        GridRow gridRow = new GridRow(row);
        gridRow.initGrid(maxWidth, maxHeight);

        return BuildResult.<GridRow, Checkbox>builder()
            .result(gridRow)
            .element(BuildResult.NO_KEY, checkbox)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public Builder translate(Definition definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public LabeledCheckboxBuilder build() {
            assert definition != null : "definition must not be null";

            return new LabeledCheckboxBuilder(definition);
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable {

        private LabelBuilder.Definition labelDefinition;
        private CheckboxBuilder.Definition checkboxDefinition;

        @Override
        public String getKey() {
            return labelDefinition.getKey();
        }

        @Override
        public boolean isTranslate() {
            return labelDefinition.isTranslate();
        }

        @Override
        public void setTitle(String title) {
            labelDefinition.setTitle(title);
        }

        @Override
        public void setDescription(String description) {
            labelDefinition.setDescription(description);
        }
    }
}
