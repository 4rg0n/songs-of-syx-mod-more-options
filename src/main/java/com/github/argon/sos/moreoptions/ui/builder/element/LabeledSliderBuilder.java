package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.i18n.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.List;

@RequiredArgsConstructor
public class LabeledSliderBuilder implements UiBuilder<List<GuiSection>, Slider> {
    private final Definition definition;

    public BuildResult<List<GuiSection>, Slider> build() {

        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }

        Slider slider = SliderBuilder.builder()
            .definition(definition.getSliderDefinition())
            .build().getResult();
        slider.pad(10, 5);

        List<GuiSection> row = LabeledBuilder.builder()
            .translate(LabeledBuilder.Definition.builder()
                .labelDefinition(definition.getLabelDefinition())
                .element(slider)
                .build())
            .build()
            .getResult();

        return BuildResult.<List<GuiSection>, Slider>builder()
            .result(row)
            .interactable(slider)
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

        public BuildResult<List<GuiSection>, Slider> build() {
            assert definition != null : "definition must not be null";

            return new LabeledSliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable<String> {

        private LabelBuilder.Definition labelDefinition;
        private SliderBuilder.Definition sliderDefinition;

        @lombok.Builder.Default
        private int labelWidth = 0;

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
