package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.*;
import com.github.argon.sos.moreoptions.ui.builder.section.BoosterSlidersBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import game.boosting.BoostableCat;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.Scrollable;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;
import util.gui.table.GScrollRows;
import util.gui.table.GScrollable;
import util.info.INFO;

import java.util.*;
import java.util.stream.Collectors;

public class BoostersPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();

    public BoostersPanel(List<Entry> boosterEntries, String configFilePath) {


        GHeader disclaimer = new GHeader("High values can slow or even crash your game");
        disclaimer.hoverInfoSet(new INFO("In case of a crash", "Delete configuration file in:" + configFilePath));

        addDown(0, disclaimer);

        GuiSection section = new GuiSection();

        List<RENDEROBJ> renderobjs = new LinkedList<>();

        Map<String, List<Entry>> groupedBoosterEntries = new HashMap<>();
        for (Entry entry : boosterEntries) {
            if(entry.getCat() != null && entry.getCat().name != null) {
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
                            .sliderDefinition(SliderBuilder.Definition.builder()
                                    .maxWidth(300)
                                    .min(entry.getRange().getMin())
                                    .max(entry.getRange().getMax())
                                    .value(entry.getRange().getValue())
                                    .valueDisplay(Slider.ValueDisplay.valueOf(entry.getRange().getDisplayMode().name()))
                                    .threshold(1000, COLOR.YELLOW100.shade(0.7d))
                                    .threshold(5000, COLOR.ORANGE100.shade(0.7d))
                                    .threshold(7500, COLOR.RED100.shade(0.7d))
                                    .threshold(9000, COLOR.RED2RED)
                                    .build())
                            .build())));

        }

        int width = 0;

        Map<String, Slider> elements = new HashMap<>();
        Map<String, List<List<? extends GuiSection>>> mapRows = new HashMap<>();
        List<List<? extends GuiSection>> rows = new ArrayList<>();

        for(String keyDef: boosterDefinitions.keySet()) { //it's stupid, but it works
            List<List<? extends GuiSection>> innerList = new ArrayList<>();
            boosterDefinitions.get(keyDef).entrySet()
                    .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new)).forEach((key, definition) -> {
                BuildResult<List<GuiSection>, Slider> buildResult = BoosterSliderBuilder.builder()
                        .definition(definition)
                        .build();
                rows.add(buildResult.getResult());
                elements.put(key, buildResult.getInteractable());
                innerList.add(buildResult.getResult());

            });
            mapRows.put(keyDef, innerList);
        }

        width = UiUtil.getMaxCombinedColumnWidth(rows);

        for(String key: boosterDefinitions.keySet()) {
            GHeader header = new GHeader(key, UI.FONT().H2);
            header.hoverInfoSet(key);

            renderobjs.add(header);

            List<List<? extends GuiSection>> innerRows = mapRows.get(key);
            List<Integer> maxWidths = UiUtil.getMaxColumnWidths(innerRows);


            int finalWidth = width;
            List<GridRow> gridRows = innerRows.stream()
                    .map(columns -> {
                        GridRow gridRow = new GridRow(columns);
                        gridRow.initGrid(maxWidths, finalWidth);

                        if (innerRows.indexOf(columns) % 2 == 0) {
                            gridRow.background(COLOR.WHITE15);
                        }

                        return gridRow;
                    })
                    .collect(Collectors.toList());

            renderobjs.addAll(gridRows);

            for(String sliderKey : elements.keySet()) {
                Slider slider = elements.get(sliderKey);
                if (!groupedBoosterEntries.containsKey(sliderKey)) {
                    sliders.put(sliderKey, slider);
                }
            }
        }
        GScrollRows gScrollRows = new GScrollRows(renderobjs, 500, width);
        section.add(gScrollRows.view());
        addDown(5, section);
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
