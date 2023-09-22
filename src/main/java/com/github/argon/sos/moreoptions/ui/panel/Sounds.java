package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Sounds extends GuiSection {
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();
    public Sounds(MoreOptionsConfig.AmbienceSounds soundsConfig) {
        GuiSection sliderSection = ambienceSoundSliders(soundsConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, new GHeader("Ambience Sounds"));
        section.addDown(10, sliderSection);

        addDownC(0, section);
    }

    private GuiSection ambienceSoundSliders(MoreOptionsConfig.AmbienceSounds soundsConfig) {
        Map<String, SliderBuilder.SliderDescription> ambienceSoundSliders = new TreeMap<>();
        ambienceSoundSliders.put("wind", SliderBuilder.SliderDescription.builder()
            .title("Wind")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNature())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("windTrees", SliderBuilder.SliderDescription.builder()
            .title("Wind Trees")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWindTrees())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("windHowl", SliderBuilder.SliderDescription.builder()
            .title("Wind Howl")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWindHowl())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("nature", SliderBuilder.SliderDescription.builder()
            .title("Nature")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNature())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("night", SliderBuilder.SliderDescription.builder()
            .title("Night")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNight())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("water", SliderBuilder.SliderDescription.builder()
            .title("Water")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWater())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("rain", SliderBuilder.SliderDescription.builder()
            .title("Rain")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNature())
            .description("TODO")
            .build());
        ambienceSoundSliders.put("thunder", SliderBuilder.SliderDescription.builder()
            .title("Nature")
            .min(0)
            .max(100)
            .input(true)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getThunder())
            .description("TODO")
            .build());

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(ambienceSoundSliders);
        sliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }
}
