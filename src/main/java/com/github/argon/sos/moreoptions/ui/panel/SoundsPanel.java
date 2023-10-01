package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.section.SlidersBuilder;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.Map;
import java.util.stream.Collectors;

public class SoundsPanel extends GuiSection {
    private static final Logger log = Loggers.getLogger(SoundsPanel.class);

    private final Map<String, Slider> ambienceSoundSliders;
    private final Map<String, Slider> settlementSoundSliders;
    private final Map<String, Slider> roomSoundSliders;
    public SoundsPanel(
        Map<String, Integer> soundsAmbienceConfig,
        Map<String, Integer> soundsSettlementConfig,
        Map<String, Integer> soundsRoomConfig
    ) {
        BuildResult<GuiSection, Slider> ambienceSlidersResult = SlidersBuilder.builder()
            .displayHeight(150)
            .defaults(soundsAmbienceConfig)
            .build().build();
        GuiSection ambienceSoundSection = ambienceSlidersResult.getResult();
        this.ambienceSoundSliders = ambienceSlidersResult.getElements();

        BuildResult<GuiSection, Slider> settlementSlidersResult = SlidersBuilder.builder()
            .displayHeight(150)
            .defaults(soundsSettlementConfig)
            .build().build();
        GuiSection settlementSoundSection = settlementSlidersResult.getResult();
        this.settlementSoundSliders = settlementSlidersResult.getElements();

        BuildResult<GuiSection, Slider> roomSlidersResult = SlidersBuilder.builder()
            .displayHeight(150)
            .defaults(soundsRoomConfig)
            .build().build();
        GuiSection roomSoundSection = roomSlidersResult.getResult();
        this.roomSoundSliders = roomSlidersResult.getElements();

        GuiSection section = new GuiSection();
        GHeader ambienceSoundsHeader = new GHeader("Ambience Sounds");
        ambienceSoundsHeader.hoverInfoSet("Ambience Sounds playing in your settlement");
        section.addDown(0, ambienceSoundsHeader);
        section.addDown(5, ambienceSoundSection);

        GHeader settlementSoundsHeader = new GHeader("Settlement Sounds");
        settlementSoundsHeader.hoverInfoSet("Sounds playing in your settlement");
        section.addDown(10, settlementSoundsHeader);
        section.addDown(5, settlementSoundSection);

        GHeader roomSoundsHeader = new GHeader("Room Sounds");
        roomSoundsHeader.hoverInfoSet("Sounds playing from buildings in your settlement");
        section.addDown(10, roomSoundsHeader);
        section.addDown(5, roomSoundSection);

        addDownC(0, section);

        applyConfig(soundsAmbienceConfig, soundsSettlementConfig, soundsRoomConfig);

    }

    public Map<String, Integer> getSoundsAmbienceConfig() {
        return ambienceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public Map<String, Integer> getSoundsSettlementConfig() {
        return settlementSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public Map<String, Integer> getSoundsRoomConfig() {
        return roomSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, slider -> slider.getValue().getValue()));
    }

    public void applyConfig(
        Map<String, Integer> soundsAmbienceConfig,
        Map<String, Integer> soundsSettlementConfig,
        Map<String, Integer> soundsRoomConfig
    ) {
        log.trace("Applying UI ambience sounds config %s", soundsAmbienceConfig);
        log.trace("Applying UI settlement sounds config %s", soundsSettlementConfig);
        log.trace("Applying UI room sounds config %s", soundsRoomConfig);

        soundsAmbienceConfig.forEach((key, value) -> {
            if (ambienceSoundSliders.containsKey(key)) {
                ambienceSoundSliders.get(key).setValue(value);
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        soundsSettlementConfig.forEach((key, value) -> {
            if (settlementSoundSliders.containsKey(key)) {
                settlementSoundSliders.get(key).setValue(value);
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        soundsRoomConfig.forEach((key, value) -> {
            if (roomSoundSliders.containsKey(key)) {
                roomSoundSliders.get(key).setValue(value);
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }
}
