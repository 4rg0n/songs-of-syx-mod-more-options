package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Toggler;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.BoosterSlidersBuilder;
import game.boosting.BoostableCat;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.table.GScrollRows;

import java.util.*;
import java.util.stream.Collectors;

public class BoostersPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Toggler<Integer>> sliders;

    public BoostersPanel(List<Entry> boosterEntries) {
        Map<String, List<Entry>> groupedBoosterEntries = new HashMap<>();
        for (Entry entry : boosterEntries) {
            if (entry.getCat() != null && entry.getCat().name != null) {
                String catName = entry.getCat().name.toString();
                // Check if the map already has a list for this cat name
                if (!groupedBoosterEntries.containsKey(catName)) {
                    // If not, create a new list and add it to the map
                    groupedBoosterEntries.put(catName, new ArrayList<>());
                }
                // Add the entry to the appropriate list
                groupedBoosterEntries.get(catName).add(entry);
            }
        }

        Map<String, Map<String, BoosterSliderBuilder.Definition>> boosterDefinitions = new HashMap<>();

        for(String cat : groupedBoosterEntries.keySet()) {
            List<Entry> groupedBoostersByCat = groupedBoosterEntries.get(cat);

            boosterDefinitions.put(cat, groupedBoostersByCat.stream().collect(Collectors.toMap(
                    Entry::getKey,
                    entry -> BoosterSliderBuilder.Definition.builder()
                            .labelDefinition(LabelBuilder.Definition.builder()
                                    .key(entry.getKey())
                                    .title(entry.getKey())
                                    .build())
                            .sliderAdditiveiDefinition(SliderBuilder.Definition.builder()
                                    .maxWidth(300)
                                    // fixme: wrong ranges
                                    .min(entry.getRange().getMin())
                                    .max(entry.getRange().getMax())
                                    .value(entry.getRange().getValue())
                                    .valueDisplay(Slider.ValueDisplay.ABSOLUTE)

//                                    .threshold((int) (0.10 *entry.getRange().getMax()), COLOR.YELLOW100.shade(0.7d))
//                                    .threshold((int) (0.50 *entry.getRange().getMax()), COLOR.ORANGE100.shade(0.7d))
//                                    .threshold((int) (0.75 *entry.getRange().getMax()), COLOR.RED100.shade(0.7d))
//                                    .threshold((int) (0.90 *entry.getRange().getMax()), COLOR.RED2RED)
                                    .build())
                            .sliderMultiDefinition(SliderBuilder.Definition.builder()
                                    .maxWidth(300)
                                    .min(entry.getRange().getMin())
                                    .max(entry.getRange().getMax())
                                    .value(entry.getRange().getValue())
                                    .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
                                    .threshold((int) (0.10 *entry.getRange().getMax()), COLOR.YELLOW100.shade(0.7d))
                                    .threshold((int) (0.50 *entry.getRange().getMax()), COLOR.ORANGE100.shade(0.7d))
                                    .threshold((int) (0.75 *entry.getRange().getMax()), COLOR.RED100.shade(0.7d))
                                    .threshold((int) (0.90 *entry.getRange().getMax()), COLOR.RED2RED)
                                    .build())
                            .build())));

        }

        BuildResult<GScrollRows, Map<String, Toggler<Integer>>> buildResult = BoosterSlidersBuilder.builder()
                .displayHeight(500)
                .translate(boosterDefinitions)
                .build();

        GScrollRows gScrollRows = buildResult.getResult();
        sliders = buildResult.getInteractable();
        addDown(10, gScrollRows.view());
    }

    public Map<String, Integer> getConfig() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(Map<String, Integer> config) {
        log.trace("Applying Booster config %s", config);

        config.forEach((key, value) -> {
            if (sliders.containsKey(key)) {
                sliders.get(key).setValue(value);
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    @Data
    @Builder
    public static class Entry {
        private MoreOptionsConfig.Range range;

        private String key;

        //  |\__/,|   (`\
        // |_ _  |.--.) )
        // ( T   )     /
        //(((^_(((/(((_/
        @Builder.Default
        private BoostableCat cat = null;
    }
}
