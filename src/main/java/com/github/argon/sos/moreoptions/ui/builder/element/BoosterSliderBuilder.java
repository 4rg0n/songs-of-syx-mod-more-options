package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.UiInfo;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.List;

@RequiredArgsConstructor
public class BoosterSliderBuilder implements UiBuilder<List<GuiSection>, Tabulator<String, Slider, Integer>> {
    private final Definition definition;

    public BuildResult<List<GuiSection>, Tabulator<String, Slider, Integer>> build() {

        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }

        GuiSection label = LabelBuilder.builder()
            .translate(definition.getLabelDefinition())
            .build().getResult();
        label.pad(10, 5);

        Slider additiveSlider = SliderBuilder.builder()
            .definition(definition.getSliderAddDefinition())
            .build().getResult();
        additiveSlider.pad(10, 5);

        Slider multiSlider = SliderBuilder.builder()
            .definition(definition.getSliderMultiDefinition())
            .build().getResult();
        multiSlider.pad(10, 5);

        Tabulator<String, Slider, Integer> tabulator = new Tabulator<>(Maps.of(
            UiInfo.<String>builder()
                .key("add")
                .title("Add")
                .description("Adds to the booster value.")
                .build(), additiveSlider,
            UiInfo.<String>builder()
                .key("multi")
                .title("Perc")
                .description("Regulates the percentage of the booster value. Values under 100% will lower the effect.")
                .build(), multiSlider
        ), DIR.W, 0, 0, false, true);

        tabulator.tab(definition.getActiveKey());
        List<GuiSection> row = Lists.of(
            label,
            tabulator
        );

        return BuildResult.<List<GuiSection>, Tabulator<String, Slider, Integer>>builder()
            .result(row)
            .interactable(tabulator)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private Definition definition;

        public Builder translate(Definition definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public BuildResult<List<GuiSection>, Tabulator<String, Slider, Integer>> build() {
            assert definition != null : "definition must not be null";

            return new BoosterSliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable<String> {

        private LabelBuilder.Definition labelDefinition;
        private SliderBuilder.Definition sliderMultiDefinition;
        private SliderBuilder.Definition sliderAddDefinition;

        @lombok.Builder.Default
        private int labelWidth = 0;

        private String activeKey;

        @Override
        public String getKey() {
            return labelDefinition.getKey();
        }

        @Override
        public boolean isTranslatable() {
            return labelDefinition.isTranslatable();
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
