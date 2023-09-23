package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WeatherPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(WeatherPanel.class);
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();
    public WeatherPanel(MoreOptionsConfig.Weather weatherConfig) {
        GuiSection sliderSection = weatherSliders(weatherConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, sliderSection);
        addDownC(0, section);
    }


    public MoreOptionsConfig.Weather getConfig() {
        return MoreOptionsConfig.Weather.builder()
            .clouds(sliders.get("clouds").getValue())
            .ice(sliders.get("ice").getValue())
            .snow(sliders.get("snow").getValue())
            .thunder(sliders.get("thunder").getValue())
            .rain(sliders.get("rain").getValue())
            .build();
    }

    public void applyConfig(MoreOptionsConfig.Weather config) {
        log.trace("Applying config %s", config);

        sliders.get("clouds").setValue(config.getClouds());
        sliders.get("ice").setValue(config.getIce());
        sliders.get("snow").setValue(config.getSnow());
        sliders.get("thunder").setValue(config.getThunder());
        sliders.get("rain").setValue(config.getRain());
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
