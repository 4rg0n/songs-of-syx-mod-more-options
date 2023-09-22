package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Weather extends GuiSection {
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();
    public Weather(MoreOptionsConfig.Weather weatherConfig) {
        GuiSection sliderSection = weatherSliders(weatherConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);
    }

    private GuiSection weatherSliders(MoreOptionsConfig.Weather weatherConfig) {
        Map<String, SliderBuilder.SliderDescription> weatherSliders = new TreeMap<>();

        weatherSliders.put("rain", SliderBuilder.SliderDescription.builder()
            .title("Rain")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(weatherConfig.getRain())
            .description("TODO")
            .build());
        weatherSliders.put("snow", SliderBuilder.SliderDescription.builder()
            .title("Snow")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(weatherConfig.getRain())
            .description("TODO")
            .build());
        weatherSliders.put("ice", SliderBuilder.SliderDescription.builder()
            .title("Ice")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(weatherConfig.getRain())
            .description("TODO")
            .build());
        weatherSliders.put("clouds", SliderBuilder.SliderDescription.builder()
            .title("Clouds")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(weatherConfig.getRain())
            .description("TODO")
            .build());
        weatherSliders.put("thunder", SliderBuilder.SliderDescription.builder()
            .title("Thunder")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(weatherConfig.getRain())
            .description("TODO")
            .build());

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(weatherSliders);
        sliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }
}
