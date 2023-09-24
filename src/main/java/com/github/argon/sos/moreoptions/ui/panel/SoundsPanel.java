package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.SliderBuilder;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SoundsPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(SoundsPanel.class);

    @Getter
    private final Map<String, Slider> ambienceSoundSliders = new HashMap<>();
    private final Map<String, Slider> settlementSoundSliders = new HashMap<>();
    public SoundsPanel(
        Map<String, Integer> soundsAmbienceConfig,
        Map<String, Integer> soundsSettlementConfig
    ) {
        GuiSection ambienceSoundSliders = ambienceSoundSliders(soundsAmbienceConfig);
        GuiSection settlementSoundSliders = settlementSoundSliders(soundsSettlementConfig);

        GuiSection section = new GuiSection();
        section.addDown(0, new GHeader("Ambience Sounds"));
        section.addDown(10, ambienceSoundSliders);

        section.addDown(25, new GHeader("Settlement Sounds"));
        section.addDown(10, settlementSoundSliders);

        addDownC(0, section);

        applyConfig(soundsAmbienceConfig, soundsSettlementConfig);

    }

    public Map<String, Integer> getSoundsAmbienceConfig() {
        return ambienceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public Map<String, Integer> getSoundsSettlementConfig() {
        return settlementSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(
        Map<String, Integer> soundsAmbienceConfig,
        Map<String, Integer> soundsSettlementConfig
    ) {
        log.trace("Applying ambience sounds config %s", soundsAmbienceConfig);
        log.trace("Applying settlement sounds config %s", soundsSettlementConfig);

        soundsAmbienceConfig.forEach((key, value) -> {
            ambienceSoundSliders.get(key).setValue(value);
        });

        soundsSettlementConfig.forEach((key, value) -> {
            settlementSoundSliders.get(key).setValue(value);
        });
    }

    private GuiSection settlementSoundSliders(Map<String, Integer> soundsSettlementConfig) {
        Map<String, SliderBuilder.Definition> settlementSoundSliders = soundsSettlementConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> SliderBuilder.Definition.builder()
                .title(config.getKey())
                .build()));

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(settlementSoundSliders, 200);
        this.settlementSoundSliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }

    private GuiSection ambienceSoundSliders(Map<String, Integer> ambienceSoundsConfig) {
        Map<String, SliderBuilder.Definition> ambienceSoundSliders = ambienceSoundsConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> SliderBuilder.Definition.builder()
                .title(config.getKey())
                .build()));

        SliderBuilder sliderBuilder = new SliderBuilder();
        GuiSection sliderSection = sliderBuilder.build(ambienceSoundSliders, 200);
        this.ambienceSoundSliders.putAll(sliderBuilder.getSliders());
        return sliderSection;
    }
}
