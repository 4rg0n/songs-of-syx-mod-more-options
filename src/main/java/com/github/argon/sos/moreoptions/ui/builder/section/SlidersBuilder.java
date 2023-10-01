package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.ScrollableBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.table.GScrollRows;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SlidersBuilder implements UiBuilder<GuiSection, Slider> {
    private final Map<String, LabeledSliderBuilder.Definition> definitions;

    private final int displayHeight;


    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link SliderBuilder.Definition}
     */
    public BuildResult<GuiSection, Slider> build() {
        Map<String, Slider> elements = new HashMap<>();
        // sort by label title
        LinkedHashMap<String, LabeledSliderBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<GridRow> gridRows = new ArrayList<>();

        definitions.forEach((key, definition) -> {
            BuildResult<GridRow, Slider> buildResult = LabeledSliderBuilder.builder().definition(definition).build().build();
            Slider slider = buildResult.getElement().orElseThrow(() -> new RuntimeException(""));

            elements.put(key, slider);
            gridRows.add(buildResult.getResult());
        });

        int maxWidth = UiUtil.getMaxColumnWidth(gridRows);
        int maxHeight = UiUtil.getMaxColumnHeight(gridRows);

        for (int i = 0; i < gridRows.size(); i++) {
            GridRow row = gridRows.get(i);
            row.initGrid(maxWidth, maxHeight);

            if (i % 2 == 0) {
                row.background(COLOR.WHITE15);
            }
        }

        // build scrollable ui elements
        GScrollRows gScrollRows = ScrollableBuilder.builder()
            .height(displayHeight)
            .rows(gridRows)
            .build().build().getResult();
        GuiSection section = new GuiSection();
        section.add(gScrollRows.view());

        return BuildResult.<GuiSection, Slider>builder()
            .result(section)
            .elements(elements)
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


        public Builder defaults(Map<String, Integer> sliderConfigs) {
            Map<String, LabeledSliderBuilder.Definition> sliderDefinitions = sliderConfigs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                config -> LabeledSliderBuilder.Definition.builder()
                    .labelDefinition(LabelBuilder.Definition.builder()
                        .key(config.getKey())
                        .title(config.getKey())
                        .build())
                    .sliderDefinition(SliderBuilder.Definition.builder().build())
                    .build()));

            return definitions(sliderDefinitions);
        }

        public Builder translate(Map<String, LabeledSliderBuilder.Definition> definitions) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definitions.values());

            return definitions(definitions);
        }

        public SlidersBuilder build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new SlidersBuilder(definitions, displayHeight);
        }
    }
}
