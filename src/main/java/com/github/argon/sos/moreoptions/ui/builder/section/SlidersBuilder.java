package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SlidersBuilder implements UiBuilder<GuiSection, Map<String, Slider>> {
    private final Map<String, LabeledSliderBuilder.Definition> definitions;

    private final int displayHeight;


    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link SliderBuilder.Definition}
     */
    public BuildResult<GuiSection, Map<String, Slider>> build() {
        Map<String, Slider> elements = new HashMap<>();
        // sort by label title
        LinkedHashMap<String, LabeledSliderBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<List<? extends GuiSection>> rows = new ArrayList<>();
        definitions.forEach((key, definition) -> {
            BuildResult<List<GuiSection>, Slider> buildResult = LabeledSliderBuilder.builder()
                .definition(definition)
                .build();

            Slider slider = buildResult.getInteractable();

            elements.put(key, slider);
            rows.add(buildResult.getResult());
        });

        GuiSection table = TableBuilder.builder()
            .evenOdd(true)
            .displayHeight(displayHeight)
            .rows(rows)
            .build()
            .getResult();

        return BuildResult.<GuiSection, Map<String, Slider>>builder()
            .result(table)
            .interactable(elements)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Map<String, LabeledSliderBuilder.Definition> definitions;

        @lombok.Setter
        @Accessors(fluent = true)
        private int displayHeight = 100;


        public Builder defaults(Map<String, MoreOptionsConfig.Range> sliderConfigs) {
            Map<String, LabeledSliderBuilder.Definition> sliderDefinitions = sliderConfigs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                config -> LabeledSliderBuilder.Definition.builder()
                    .labelDefinition(LabelBuilder.Definition.builder()
                        .key(config.getKey())
                        .title(config.getKey())
                        .build())
                    .sliderDefinition(SliderBuilder.Definition.buildFrom(config.getValue())
                        .maxWidth(300)
                        .build())
                    .build()));

            return definitions(sliderDefinitions);
        }

        public Builder translate(Map<String, LabeledSliderBuilder.Definition> definitions) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definitions.values());

            return definitions(definitions);
        }

        public BuildResult<GuiSection, Map<String, Slider>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new SlidersBuilder(definitions, displayHeight).build();
        }
    }
}
