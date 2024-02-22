package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Toggler;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
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

public class BoostersPanel extends GuiSection implements Valuable<Void> {
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
        groupedBoosterEntries.keySet().forEach(cat -> {
            List<Entry> groupedBoostersByCat = groupedBoosterEntries.get(cat);
            boosterDefinitions.put(cat, groupedBoostersByCat.stream()
                .collect(Collectors.toMap(
                    Entry::getKey,
                    BoostersPanel::buildSliderDefinition)));
        });

        BuildResult<GScrollRows, Map<String, Toggler<Integer>>> buildResult = BoosterSlidersBuilder.builder()
                .displayHeight(500)
                .translate(boosterDefinitions)
                .build();

        GScrollRows gScrollRows = buildResult.getResult();
        sliders = buildResult.getInteractable();
        addDown(10, gScrollRows.view());
    }

    public Map<String, MoreOptionsConfig.Range> getConfig() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Toggler<Integer> toggler = entry.getValue();
                    return MoreOptionsConfig.Range.builder()
                        .value(toggler.getValue())
                        .applyMode(MoreOptionsConfig.Range.ApplyMode.valueOf(
                            toggler.getActiveInfo().getKey().toUpperCase()))
                        .build();
                }));
    }

    public void applyConfig(Map<String, MoreOptionsConfig.Range> config) {
        log.trace("Applying Booster config %s", config);

        config.forEach((key, range) -> {
            if (sliders.containsKey(key)) {
                Toggler<Integer> toggler = sliders.get(key);
                toggler.reset();
                toggler.toggle(range.getApplyMode().name().toLowerCase());
                toggler.setValue(range.getValue());

            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    private static BoosterSliderBuilder.Definition buildSliderDefinition(Entry entry) {
        MoreOptionsConfig.Range rangeMulti;
        MoreOptionsConfig.Range rangeAdd;

        if (entry.getRange().getApplyMode().equals(MoreOptionsConfig.Range.ApplyMode.MULTI)) {
            rangeMulti = entry.getRange();
            rangeAdd = MoreOptionsScript.getConfigStore()
                .getDefaults().getBoostersAdd().get(entry.getKey());
        } else {
            rangeMulti = MoreOptionsScript.getConfigStore()
                .getDefaults().getBoostersMulti().get(entry.getKey());
            rangeAdd = entry.getRange();
        }

        return BoosterSliderBuilder.Definition.builder()
            .labelDefinition(LabelBuilder.Definition.builder()
                .key(entry.getKey())
                .title(entry.getKey())
                .build())
            .sliderAddDefinition(SliderBuilder.Definition.builder()
                .maxWidth(300)
                .min(rangeAdd.getMin())
                .max(rangeAdd.getMax())
                .value(rangeAdd.getValue())
                .valueDisplay(Slider.ValueDisplay.ABSOLUTE)
                .threshold((int) (0.10 * rangeAdd.getMax()), COLOR.YELLOW100.shade(0.7d))
                .threshold((int) (0.50 * rangeAdd.getMax()), COLOR.ORANGE100.shade(0.7d))
                .threshold((int) (0.75 * rangeAdd.getMax()), COLOR.RED100.shade(0.7d))
                .threshold((int) (0.90 * rangeAdd.getMax()), COLOR.RED2RED)
                .build())
            .sliderMultiDefinition(SliderBuilder.Definition.builder()
                .maxWidth(300)
                .min(rangeMulti.getMin())
                .max(rangeMulti.getMax())
                .value(rangeMulti.getValue())
                .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
                .threshold((int) (0.10 * rangeMulti.getMax()), COLOR.YELLOW100.shade(0.7d))
                .threshold((int) (0.50 * rangeMulti.getMax()), COLOR.ORANGE100.shade(0.7d))
                .threshold((int) (0.75 * rangeMulti.getMax()), COLOR.RED100.shade(0.7d))
                .threshold((int) (0.90 * rangeMulti.getMax()), COLOR.RED2RED)
                .build())
            .build();
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    public void setValue(Void value) {

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
