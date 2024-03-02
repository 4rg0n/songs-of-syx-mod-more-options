package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.MoreOptionsDefaults;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBoosters;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.BoosterSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.section.BoosterSlidersBuilder;
import game.boosting.BoostableCat;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains sliders to influence values of game boosters
 */
public class BoostersPanel extends GuiSection implements Valuable<Map<String, MoreOptionsV2Config.Range>, BoostersPanel> {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Tabulator<String, Slider, Integer>> slidersWithToggle;
    private final MoreOptionsDefaults defaults = MoreOptionsDefaults.getInstance();

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

        // build booster sliders with add and perc toggle
        Map<String, Map<String, BoosterSliderBuilder.Definition>> boosterDefinitions = new HashMap<>();
        groupedBoosterEntries.keySet().forEach(cat -> {
            List<Entry> groupedBoostersByCat = groupedBoosterEntries.get(cat);
            boosterDefinitions.put(cat, groupedBoostersByCat.stream()
                .collect(Collectors.toMap(
                    Entry::getKey,
                    this::buildSliderDefinition)));
        });

        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        BuildResult<Table, Map<String, Tabulator<String, Slider, Integer>>> buildResult = BoosterSlidersBuilder.builder()
            .displayHeight(600)
            .search(searchInput)
            .translate(boosterDefinitions)
            .build();


        Table boosterTable = buildResult.getResult();
        slidersWithToggle = buildResult.getInteractable();
        addDownC(0, new GInput(searchInput));
        addDownC(10, boosterTable);
    }

    @Override
    public Map<String, MoreOptionsV2Config.Range> getValue() {
        return slidersWithToggle.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsV2Config.Range.fromSlider(tab.getValue().getActiveTab())));
    }

    @Override
    public void setValue(Map<String, MoreOptionsV2Config.Range> config) {
        log.trace("Applying Booster config %s", config);

        config.forEach((key, range) -> {
            if (slidersWithToggle.containsKey(key)) {
                Tabulator<String, Slider, Integer> tabulator = slidersWithToggle.get(key);
                tabulator.reset();
                tabulator.tab(range.getApplyMode().name().toLowerCase());
                tabulator.setValue(range.getValue());

            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    private BoosterSliderBuilder.Definition buildSliderDefinition(Entry entry) {
        MoreOptionsV2Config.Range rangeMulti;
        MoreOptionsV2Config.Range rangeAdd;
        String activeKey;

        if (entry.getRange().getApplyMode().equals(MoreOptionsV2Config.Range.ApplyMode.MULTI)) {
            rangeMulti = entry.getRange();
            rangeAdd = defaults.getBoostersAdd().get(entry.getKey());
            activeKey = "multi";
        } else {
            rangeMulti = defaults.getBoostersMulti().get(entry.getKey());
            rangeAdd = entry.getRange();
            activeKey = "add";
        }

        return BoosterSliderBuilder.Definition.builder()
            .labelDefinition(LabelBuilder.Definition.builder()
                .key(entry.getKey())
                .title(entry.getKey())
                .build())
            .sliderAddDefinition(SliderBuilder.Definition.buildFrom(rangeAdd)
                .maxWidth(300)
                .threshold((int) (0.10 * rangeAdd.getMax()), COLOR.YELLOW100.shade(0.7d))
                .threshold((int) (0.50 * rangeAdd.getMax()), COLOR.ORANGE100.shade(0.7d))
                .threshold((int) (0.75 * rangeAdd.getMax()), COLOR.RED100.shade(0.7d))
                .threshold((int) (0.90 * rangeAdd.getMax()), COLOR.RED2RED)
                .build())
            .sliderMultiDefinition(SliderBuilder.Definition.buildFrom(rangeMulti)
                .maxWidth(300)
                .threshold((int) (0.10 * rangeMulti.getMax()), COLOR.YELLOW100.shade(0.7d))
                .threshold((int) (0.50 * rangeMulti.getMax()), COLOR.ORANGE100.shade(0.7d))
                .threshold((int) (0.75 * rangeMulti.getMax()), COLOR.RED100.shade(0.7d))
                .threshold((int) (0.90 * rangeMulti.getMax()), COLOR.RED2RED)
                .build())
            .boosters(entry.getBoosters())
            .activeKey(activeKey)
            .build();
    }

    @Data
    @Builder
    public static class Entry {
        private MoreOptionsV2Config.Range range;
        private String key;

        //  |\__/,|   (`\
        // |_ _  |.--.) )
        // ( T   )     /
        //(((^_(((/(((_/
        @Builder.Default
        private BoostableCat cat = null;

        private MoreOptionsBoosters boosters;
    }
}
