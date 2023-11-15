package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.BoosterSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.section.BoosterSlidersBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;
import util.info.INFO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoostersPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Slider> sliders;

    public BoostersPanel(List<Entry> boosterEntries, String configFilePath) {
        Map<String, BoosterSliderBuilder.Definition> boosterDefinitions = boosterEntries.stream().collect(Collectors.toMap(
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
                    .valueDisplay(Slider.ValueDisplay.valueOf(entry.getRange().getDisplayMode().name()))
                    .threshold(1000, COLOR.YELLOW100.shade(0.7d))
                    .threshold(5000, COLOR.ORANGE100.shade(0.7d))
                    .threshold(7500, COLOR.RED100.shade(0.7d))
                    .threshold(9000, COLOR.RED2RED)
                    .build())
                .player(entry.isPlayer())
                .enemy(entry.isEnemy())
                .build()));

        BuildResult<GuiSection, Map<String, Slider>> buildResult = BoosterSlidersBuilder.builder()
            .displayHeight(500)
            .translate(boosterDefinitions)
            .build();

        GuiSection sliderSection = buildResult.getResult();
        sliders = buildResult.getInteractable();

        GHeader disclaimer = new GHeader("High values can slow or even crash your game");
        disclaimer.hoverInfoSet(new INFO("In case of a crash", "Delete configuration file in:" + configFilePath));

        addDown(0, disclaimer);
        addDown(10, sliderSection);
    }

    public Map<String, Integer> getConfig() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(Map<String, Integer> config) {
        log.trace("Applying UI weather config %s", config);

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

        @Builder.Default
        private boolean player = false;

        @Builder.Default
        private boolean enemy = false;
    }
}
