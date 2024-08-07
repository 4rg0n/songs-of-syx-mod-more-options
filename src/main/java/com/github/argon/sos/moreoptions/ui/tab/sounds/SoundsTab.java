package com.github.argon.sos.moreoptions.ui.tab.sounds;

import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.config.domain.SoundsConfig;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.game.ui.layout.Layout;
import com.github.argon.sos.moreoptions.game.ui.layout.VerticalLayout;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import snake2d.util.color.COLOR;
import util.gui.misc.GHeader;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains slider for controlling the volume of game sound effects
 */
public class SoundsTab extends AbstractConfigTab<SoundsConfig, SoundsTab> {
    private static final Logger log = Loggers.getLogger(SoundsTab.class);
    private final static I18n i18n = I18n.get(SoundsTab.class);

    private final Map<String, Slider> ambienceSoundSliders;
    public SoundsTab(
        String title,
        SoundsConfig soundsConfig,
        SoundsConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig, availableWidth, availableHeight);
        this.ambienceSoundSliders = UiMapper.toSliders(soundsConfig.getAmbience());

        GHeader ambienceSoundsHeader = new GHeader(i18n.t("SoundsTab.header.ambienceSounds.name"));
        ambienceSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.ambienceSounds.desc"));

        GHeader roomSoundsHeader = new GHeader(i18n.t("SoundsTab.header.roomSounds.name"));
        roomSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.roomSounds.desc"));

        Map<String, Slider> ambience = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> !stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Slider> room = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int tableHeight = (availableHeight
            - ambienceSoundsHeader.body().height()
            - roomSoundsHeader.body().height()
            - 30) / 2;

        Layout.vertical(tableHeight)
            .addDownC(0, ambienceSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<Integer>builder()
                .rows(UiMapper.toLabeledColumnRows(ambience, i18n))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(this);

        Layout.vertical(tableHeight)
            .addDownC(20, roomSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<Integer>builder()
                .rows(UiMapper.getInstance().toRoomSoundLabeledColumnRows(room))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(this);
    }

    @Override
    public SoundsConfig getValue() {
        return SoundsConfig.builder()
            .ambience(getSoundsAmbienceConfig())
            .build();
    }

    public Map<String, Range> getSoundsAmbienceConfig() {
        return ambienceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                tab -> Range.fromSlider(tab.getValue())));
    }

    @Override
    public void setValue(SoundsConfig soundsConfig) {
        log.trace("Applying UI sounds config %s", soundsConfig);

        soundsConfig.getAmbience().forEach((key, range) -> {
            if (ambienceSoundSliders.containsKey(key)) {
                ambienceSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected SoundsTab element() {
        return this;
    }
}
