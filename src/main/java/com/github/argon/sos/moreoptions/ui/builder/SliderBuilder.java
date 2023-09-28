package com.github.argon.sos.moreoptions.ui.builder;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sets.LinkedList;
import util.data.INT;
import util.gui.misc.GText;
import util.gui.table.GScrollRows;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SliderBuilder {
    @Getter
    private Map<String, Slider> sliders;

    private final Dictionary dictionary = Dictionary.getInstance();

    /**
     * Uses default {@link Definition} to build a slider
     * See {@link this#build(Map, int)}
     */
    public GuiSection buildDefault(Map<String, Integer> sliderConfigs, int displayHeight) {
        Map<String, SliderBuilder.Definition> sliderDefinitions = sliderConfigs.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> SliderBuilder.Definition.builder()
                .title(config.getKey())
                .build()));

        return build(sliderDefinitions, displayHeight);
    }

    /**
     * Builds a section with a scrollable list of sliders with labels in front of them.
     * Each entry is a slider with its {@link Definition}
     */
    public GuiSection build(Map<String, Definition> sliderDefinitions, int displayHeight) {
        // reset memorized sliders
        sliders = new HashMap<>();

        // map and order entries by dictionary title
        Map<String, Dictionary.Entry> translatedEntries = sliderDefinitions.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> dictionary.get(entry.getKey())));
        Map<String, Dictionary.Entry> sortedTranslatedEntries = translatedEntries.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<List<GuiSection>> columnElements = new ArrayList<>();
        columnElements.add(new ArrayList<>());
        columnElements.add(new ArrayList<>());

        // build array with columns for labels and sliders
        sortedTranslatedEntries.forEach((key, dictEntry) -> {
            Definition definition = sliderDefinitions.get(key);

            Slider slider = buildSlider(definition);
            GuiSection label = buildLabel(dictEntry);

            columnElements.get(0).add(label);
            columnElements.get(1).add(slider);
            sliders.put(key, slider);
        });

        // determine max with of all rows inh each column
        ArrayList<Integer> maxWidths = new ArrayList<>();
        columnElements.forEach(sections -> {
            maxWidths.add(getMaxWidth(sections));
        });
        Integer maxWidth = Collections.max(maxWidths);

        // build rows with label and slider as columns
        LinkedList<ScrollableBuilder.GridRow> columnRows = new LinkedList<>();
        for (int i = 0; i < columnElements.get(0).size(); i++) {
            GuiSection label = columnElements.get(0).get(i);
            GuiSection slider = columnElements.get(1).get(i);

            ScrollableBuilder.GridRow gridRow = new ScrollableBuilder.GridRow(
                Stream.of(label, slider).collect(Collectors.toList()),
                slider.body().height(),
                maxWidth
            );

            if (columnRows.size() % 2 == 0) {
               gridRow.background(COLOR.WHITE15);
            }

            columnRows.add(gridRow);
        }

        // build scrollable ui elements
        ScrollableBuilder scrollableBuilder = new ScrollableBuilder();
        GScrollRows gScrollRows = scrollableBuilder.build(columnRows, displayHeight);
        GuiSection section = new GuiSection();
        section.add(gScrollRows.view());

        return section;
    }

    private int getMaxWidth(List<GuiSection> sections) {
        int maxWidth = 0;

        for (GuiSection section : sections) {
            int sectionWidth = section.body().width();

            if (sectionWidth > maxWidth) {
                maxWidth = sectionWidth;
            }
        }

        return maxWidth;
    }

    private GuiSection buildLabel(Dictionary.Entry dictEntry) {
        GuiSection text = new GuiSection();
        text.addRight(0, new GText(UI.FONT().M, dictEntry.getTitle()));

        if (dictEntry.getDescription() != null) {
            text.hoverInfoSet(dictEntry.getDescription());
        }

        return text;
    }

    private Slider buildSlider(Definition definition) {
        int min = definition.getMin();
        int max = definition.getMax();
        int sliderWidth = (Math.abs(min) + Math.abs(max)) * 2;

        if (definition.getMaxWidth() != 0 && sliderWidth > definition.getMaxWidth()) {
            sliderWidth = definition.getMaxWidth();
        }

        INT.IntImp sliderValue = new INT.IntImp(min, max);
        sliderValue.set(definition.getValue());
        return new Slider(sliderValue, sliderWidth, definition.isInput(), definition.isLockScroll(), definition.getValueDisplay());
    }

    @Data
    @Builder
    public static class Definition {
        private String title;
        private String description;

        @Builder.Default
        private int min = 0;

        @Builder.Default
        private int max = 100;

        private int value;

        private int maxWidth;

        @Builder.Default
        private boolean input = true;

        @Builder.Default
        private boolean lockScroll = true;

        @Builder.Default
        private Slider.ValueDisplay valueDisplay = Slider.ValueDisplay.PERCENTAGE;
    }
}
