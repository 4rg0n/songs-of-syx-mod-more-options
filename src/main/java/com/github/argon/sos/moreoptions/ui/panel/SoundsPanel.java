package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GHeader;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the volume of game sound effects
 */
public class SoundsPanel extends GuiSection implements Valuable<MoreOptionsV2Config.Sounds, SoundsPanel> {
    private static final Logger log = Loggers.getLogger(SoundsPanel.class);
    private final static I18n i18n = I18n.get(SoundsPanel.class);

    private final Map<String, Slider> ambienceSoundSliders;
    private final Map<String, Slider> settlementSoundSliders;
    private final Map<String, Slider> roomSoundSliders;
    public SoundsPanel(MoreOptionsV2Config.Sounds sounds, int availableWidth, int availableHeight) {

        this.ambienceSoundSliders = UiMapper.toSliders(sounds.getAmbience());
        this.settlementSoundSliders = UiMapper.toSliders(sounds.getSettlement());
        this.roomSoundSliders = UiMapper.toSliders(sounds.getRoom());

        GHeader ambienceSoundsHeader = new GHeader(i18n.t("SoundsPanel.header.ambienceSounds.name"));
        ambienceSoundsHeader.hoverInfoSet(i18n.d("SoundsPanel.header.ambienceSounds.desc"));
        int tableHeight = (availableHeight - (ambienceSoundsHeader.body().height() * 3 + 35)) / 3;
        Table<Integer> ambienceSoundsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(ambienceSoundSliders, i18n))
            .rowPadding(5)
            .displayHeight(tableHeight)
            .build();

        GHeader settlementSoundsHeader = new GHeader(i18n.t("SoundsPanel.header.settlementSounds.name"));
        settlementSoundsHeader.hoverInfoSet(i18n.d("SoundsPanel.header.settlementSounds.desc"));
        Table<Integer> settlementSoundsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(settlementSoundSliders, i18n))
            .rowPadding(5)
            .displayHeight(tableHeight)
            .build();

        GHeader roomSoundsHeader = new GHeader(i18n.t("SoundsPanel.header.roomSounds.name"));
        roomSoundsHeader.hoverInfoSet(i18n.d("SoundsPanel.header.roomSounds.desc"));
        Table<Integer> roomSoundsTable = Table.<Integer>builder()
            .rows(UiMapper.toLabeledColumnRows(roomSoundSliders, i18n))
            .rowPadding(5)
            .displayHeight(tableHeight)
            .build();


        addDown(0, ambienceSoundsHeader);
        addDown(5, ambienceSoundsTable);
        addDown(10, settlementSoundsHeader);
        addDown(5, settlementSoundsTable);
        addDown(10, roomSoundsHeader);
        addDown(5, roomSoundsTable);
    }

    @Override
    public MoreOptionsV2Config.Sounds getValue() {
        return MoreOptionsV2Config.Sounds.builder()
            .ambience(getSoundsAmbienceConfig())
            .settlement(getSoundsSettlementConfig())
            .room(getSoundsRoomConfig())
            .build();
    }

    public Map<String, MoreOptionsV2Config.Range> getSoundsAmbienceConfig() {
        return ambienceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsV2Config.Range.fromSlider(tab.getValue())));
    }

    public Map<String, MoreOptionsV2Config.Range> getSoundsSettlementConfig() {
        return settlementSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsV2Config.Range.fromSlider(tab.getValue())));
    }

    public Map<String, MoreOptionsV2Config.Range> getSoundsRoomConfig() {
        return roomSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> MoreOptionsV2Config.Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(MoreOptionsV2Config.Sounds sounds) {
        log.trace("Applying UI sounds config %s", sounds);

        sounds.getAmbience().forEach((key, range) -> {
            if (ambienceSoundSliders.containsKey(key)) {
                ambienceSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        sounds.getSettlement().forEach((key, range) -> {
            if (settlementSoundSliders.containsKey(key)) {
                settlementSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        sounds.getRoom().forEach((key, range) -> {
            if (roomSoundSliders.containsKey(key)) {
                roomSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }
}
