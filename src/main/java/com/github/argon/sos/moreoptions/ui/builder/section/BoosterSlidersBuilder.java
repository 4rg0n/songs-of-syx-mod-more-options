package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.BoosterSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BoosterSlidersBuilder implements UiBuilder<Table, Map<String, Tabulator<String, Slider, Integer>>> {
    private final Map<String, Map<String, BoosterSliderBuilder.Definition>> definitions;
    private final int displayHeight;
    @Nullable
    private final StringInputSprite search;


    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link SliderBuilder.Definition}
     */
    public BuildResult<Table, Map<String, Tabulator<String, Slider, Integer>>> build() {
        Map<String, Tabulator<String, Slider, Integer>> elements = new HashMap<>();
        Map<String, List<ColumnRow>> mapRows = new HashMap<>();

        for(String keyDef: definitions.keySet()) {
            List<ColumnRow> innerList = new ArrayList<>();
            definitions.get(keyDef).entrySet()
                .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                .forEach((key, definition) -> {
                    BuildResult<List<GuiSection>, Tabulator<String, Slider, Integer>> buildResult = BoosterSliderBuilder.builder()
                            .definition(definition)
                            .build();
                    ColumnRow columnRow = buildResult.toColumnRow().getResult();
                    columnRow.setSearchTerm(key);
                    elements.put(key, buildResult.getInteractable());
                    innerList.add(columnRow);
                });
            mapRows.put(keyDef, innerList);
        }

        Table table = TableBuilder.builder()
            .rowsCategorized(mapRows)
            .evenOdd(true)
            .search(search)
            .displayHeight(displayHeight)
            .build().getResult();

        return BuildResult.<Table, Map<String, Tabulator<String, Slider, Integer>>>builder()
            .result(table)
            .interactable(elements)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private Map<String, Map<String, BoosterSliderBuilder.Definition>> definitions;

        @Accessors(fluent = true)
        private int displayHeight = 100;

        @Accessors(fluent = true)
        private StringInputSprite search;

        public Builder translate(Map<String, Map<String, BoosterSliderBuilder.Definition>> definitions) {
            Dictionary dictionary = Dictionary.getInstance();

            for(Map<String, BoosterSliderBuilder.Definition>  definitionMap : definitions.values()) {
                dictionary.translate(definitionMap.values());
            }

            return definitions(definitions);
        }
        public BuildResult<Table, Map<String, Tabulator<String, Slider, Integer>>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new BoosterSlidersBuilder(definitions, displayHeight, search).build();
        }
    }
}
