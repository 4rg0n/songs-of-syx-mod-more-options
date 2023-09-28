package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.Map;
import java.util.stream.Collectors;

public class BoostersPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(BoostersPanel.class);
    @Getter
    private final Map<String, Slider> sliders;
    public BoostersPanel(Map<String, Integer> boosterConfig) {
        SliderBuilder sliderBuilder = new SliderBuilder();
        Map<String, SliderBuilder.Definition> sliderDefinitions = boosterConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> SliderBuilder.Definition.builder()
                .title(config.getKey())
                .maxWidth(200)
                .max(1000)
                .build()));

        GuiSection sliderSection = sliderBuilder.build(sliderDefinitions, 600);
        sliders = sliderBuilder.getSliders();

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);

        applyConfig(boosterConfig);
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
}
