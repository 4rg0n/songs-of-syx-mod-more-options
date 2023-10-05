package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.section.SlidersBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.Map;
import java.util.stream.Collectors;

public class WeatherPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    @Getter
    private final Map<String, Slider> sliders;
    public WeatherPanel(Map<String, Integer> weatherConfig) {
        BuildResult<GuiSection, Map<String, Slider>> slidersBuildResult = SlidersBuilder.builder()
            .displayHeight(400)
            .defaults(weatherConfig)
            .build();

        GuiSection sliderSection = slidersBuildResult.getResult();
        sliders = slidersBuildResult.getInteractable();

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);
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
