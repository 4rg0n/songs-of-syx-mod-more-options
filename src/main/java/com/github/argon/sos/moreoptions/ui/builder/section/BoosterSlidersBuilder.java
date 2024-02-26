package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.BoosterSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableHeaderBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.table.GScrollRows;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BoosterSlidersBuilder implements UiBuilder<GScrollRows, Map<String, Tabulator<String, Integer, Slider>>> {
    private final Map<String, Map<String, BoosterSliderBuilder.Definition>> definitions;
    private final int displayHeight;


    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link SliderBuilder.Definition}
     */
    public BuildResult<GScrollRows, Map<String, Tabulator<String, Integer, Slider>>> build() {

        List<RENDEROBJ> renderobjs = new LinkedList<>();
        Map<String, Tabulator<String, Integer, Slider>> elements = new HashMap<>();
        Map<String, List<List<? extends GuiSection>>> mapRows = new HashMap<>();
        List<List<? extends GuiSection>> rows = new ArrayList<>();

        for(String keyDef: definitions.keySet()) {
            List<List<? extends GuiSection>> innerList = new ArrayList<>();
            definitions.get(keyDef).entrySet()
                    .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new)).forEach((key, definition) -> {
                        BuildResult<List<GuiSection>, Tabulator<String, Integer, Slider>> buildResult = BoosterSliderBuilder.builder()
                                .definition(definition)
                                .build();
                        rows.add(buildResult.getResult());
                        elements.put(key, buildResult.getInteractable());
                        innerList.add(buildResult.getResult());

                    });
            mapRows.put(keyDef, innerList);
        }

        int widthTotal = UiUtil.getMaxCombinedColumnWidth(rows);

        for (String key: definitions.keySet()) {
            renderobjs.addAll(TableHeaderBuilder.builder()
                    .evenOdd(true)
                    .displayHeight(displayHeight)
                    .key(key)
                    .headerWithRows(mapRows)
                    .build()
                    .getResults());
        }

        GScrollRows gScrollRows = new GScrollRows(renderobjs, this.displayHeight, widthTotal);

        return BuildResult.<GScrollRows, Map<String, Tabulator<String, Integer, Slider>>>builder()
            .result(gScrollRows)
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

        public Builder translate(Map<String, Map<String, BoosterSliderBuilder.Definition>> definitions) {
            Dictionary dictionary = Dictionary.getInstance();

            for(Map<String, BoosterSliderBuilder.Definition>  definitionMap : definitions.values()) {
                dictionary.translate(definitionMap.values());
            }

            return definitions(definitions);
        }
        public BuildResult<GScrollRows, Map<String, Tabulator<String, Integer, Slider>>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new BoosterSlidersBuilder(definitions, displayHeight).build();
        }
    }
}
