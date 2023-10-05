package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.LabelBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledSliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.ui.builder.section.SlidersBuilder;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoostersPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Slider> sliders;

    public BoostersPanel(List<Entry> boosterEntries) {
        Map<String, LabeledSliderBuilder.Definition> sliderDefinitions = boosterEntries.stream().collect(Collectors.toMap(
            Entry::getKey,
            entry -> LabeledSliderBuilder.Definition.builder()
                .labelDefinition(LabelBuilder.Definition.builder()
                    .key(entry.getKey())
                    .title(entry.getKey())
                    .build())
                .sliderDefinition(SliderBuilder.Definition.builder()
                    .maxWidth(300)
                    .max(10000)
                    .build())
                .build()));

        BuildResult<GuiSection, Slider> buildResult = SlidersBuilder.builder()
            .displayHeight(550)
            .translate(sliderDefinitions)
            .build();

        GuiSection sliderSection = buildResult.getResult();
        sliders = buildResult.getElements();

        addDownC(0, new GText(UI.FONT().S, "Changing boosters to higher values can slow down the game or even crash it."));
        addDownC(15, sliderSection);
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
        private int value;

        private String key;

        @Builder.Default
        private boolean player = false;

        @Builder.Default
        private boolean enemy = false;
    }
}
