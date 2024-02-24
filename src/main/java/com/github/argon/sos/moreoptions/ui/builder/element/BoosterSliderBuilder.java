package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Toggler;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.MapUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class BoosterSliderBuilder implements UiBuilder<List<GuiSection>, Toggler<Integer>> {
    private final Definition definition;

    public BuildResult<List<GuiSection>, Toggler<Integer>> build() {

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

        Toggler<Integer> toggler = new Toggler<>(MapUtil.of(
            // todo dictionary
            Toggler.Info.builder()
                .key("add")
                .title("Add")
                .description("Adds to the booster value.")
                .build(), additiveSlider,
            Toggler.Info.builder()
                .key("multi")
                .title("Perc")
                .description("Regulates the percentage of the booster value. Values under 100% will lower the effect.")
                .build(), multiSlider
        ), DIR.W, 0, false, true);

        List<GuiSection> row = Stream.of(
            label,
            toggler
        ).collect(Collectors.toList());

        return BuildResult.<List<GuiSection>, Toggler<Integer>>builder()
            .result(row)
            .interactable(toggler)
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

        public BuildResult<List<GuiSection>, Toggler<Integer>> build() {
            assert definition != null : "definition must not be null";

            return new BoosterSliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable {

        private LabelBuilder.Definition labelDefinition;
        private SliderBuilder.Definition sliderMultiDefinition;
        private SliderBuilder.Definition sliderAddDefinition;


        @lombok.Builder.Default
        private int labelWidth = 0;

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
