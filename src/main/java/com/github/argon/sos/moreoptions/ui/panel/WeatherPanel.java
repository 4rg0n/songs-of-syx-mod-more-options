package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();
    public WeatherPanel(Map<String, Integer> weatherConfig) {
        GuiSection sliderSection = weatherSliders(weatherConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);

        applyConfig(weatherConfig);
    }


    public Map<String, Integer> getConfig() {
        return sliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(Map<String, Integer> config) {
        log.trace("Applying weather config %s", config);

        config.forEach((key, value) -> {
            sliders.get(key).setValue(value);
        });
    }

   private GuiSection weatherSliders(Map<String, Integer> weatherConfig) {
       Map<String, SliderBuilder.Definition> weatherSliders = weatherConfig.entrySet().stream().collect(Collectors.toMap(
           Map.Entry::getKey,
           config -> SliderBuilder.Definition.builder()
               .title(config.getKey())
               .build()));

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(weatherSliders, 400);
        sliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }
}
