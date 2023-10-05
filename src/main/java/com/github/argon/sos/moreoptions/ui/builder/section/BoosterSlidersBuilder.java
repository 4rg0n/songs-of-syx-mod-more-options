package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.BoosterSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BoosterSlidersBuilder implements UiBuilder<GuiSection, Map<String, Slider>> {
    private final Map<String, BoosterSliderBuilder.Definition> definitions;

    private final int displayHeight;


    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link SliderBuilder.Definition}
     */
    public BuildResult<GuiSection, Map<String, Slider>> build() {
        Map<String, Slider> elements = new HashMap<>();
        // sort by label title
        LinkedHashMap<String, BoosterSliderBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<List<? extends GuiSection>> rows = new ArrayList<>();
        definitions.forEach((key, definition) -> {
            BuildResult<List<GuiSection>, Slider> buildResult = BoosterSliderBuilder.builder()
                .definition(definition)
                .build();
            elements.put(key, buildResult.getInteractable());
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
        private Map<String, BoosterSliderBuilder.Definition> definitions;

        @lombok.Setter
        @Accessors(fluent = true)
        private int displayHeight = 100;

        public Builder translate(Map<String, BoosterSliderBuilder.Definition> definitions) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definitions.values());

            return definitions(definitions);
        }

        public BuildResult<GuiSection, Map<String, Slider>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new BoosterSlidersBuilder(definitions, displayHeight).build();
        }
    }
}
