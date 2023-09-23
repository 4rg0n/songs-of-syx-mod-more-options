package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SoundsPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(SoundsPanel.class);

    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();
    public SoundsPanel(MoreOptionsConfig.AmbienceSounds soundsConfig) {
        GuiSection sliderSection = ambienceSoundSliders(soundsConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, new GHeader("Ambience Sounds"));
        section.addDown(10, sliderSection);

        addDownC(0, section);
    }

    public MoreOptionsConfig.AmbienceSounds getConfig() {
        return MoreOptionsConfig.AmbienceSounds.builder()
            .nature(sliders.get("nature").getValue())
            .night(sliders.get("night").getValue())
            .water(sliders.get("water").getValue())
            .wind(sliders.get("wind").getValue())
            .windHowl(sliders.get("windHowl").getValue())
            .windTrees(sliders.get("windTrees").getValue())
            .rain(sliders.get("rain").getValue())
            .thunder(sliders.get("thunder").getValue())
            .build();
    }

    public void applyConfig(MoreOptionsConfig.AmbienceSounds config) {
        log.trace("Applying config %s", config);

        sliders.get("nature").setValue(config.getNature());
        sliders.get("night").setValue(config.getNight());
        sliders.get("water").setValue(config.getWater());
        sliders.get("wind").setValue(config.getWind());
        sliders.get("windHowl").setValue(config.getWindHowl());
        sliders.get("windTrees").setValue(config.getWindTrees());
        sliders.get("rain").setValue(config.getRain());
        sliders.get("thunder").setValue(config.getThunder());
    }

    private GuiSection ambienceSoundSliders(MoreOptionsConfig.AmbienceSounds soundsConfig) {
        Map<String, SliderBuilder.SliderDescription> ambienceSoundSliders = new TreeMap<>();
        ambienceSoundSliders.put("wind", SliderBuilder.SliderDescription.builder()
            .title("Wind")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWind())
            .build());
        ambienceSoundSliders.put("windTrees", SliderBuilder.SliderDescription.builder()
            .title("Wind Trees")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWindTrees())
            .build());
        ambienceSoundSliders.put("windHowl", SliderBuilder.SliderDescription.builder()
            .title("Wind Howl")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWindHowl())
            .build());
        ambienceSoundSliders.put("nature", SliderBuilder.SliderDescription.builder()
            .title("Nature")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNature())
            .build());
        ambienceSoundSliders.put("night", SliderBuilder.SliderDescription.builder()
            .title("Night")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNight())
            .build());
        ambienceSoundSliders.put("water", SliderBuilder.SliderDescription.builder()
            .title("Water")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getWater())
            .build());
        ambienceSoundSliders.put("rain", SliderBuilder.SliderDescription.builder()
            .title("Rain")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getNature())
            .build());
        ambienceSoundSliders.put("thunder", SliderBuilder.SliderDescription.builder()
            .title("Thunder")
            .min(0)
            .max(100)
            .valueDisplay(Slider.ValueDisplay.PERCENTAGE)
            .value(soundsConfig.getThunder())
            .build());

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(ambienceSoundSliders);
        sliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }
}
