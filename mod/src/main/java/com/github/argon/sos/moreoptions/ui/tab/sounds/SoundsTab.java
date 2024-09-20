package com.github.argon.sos.moreoptions.ui.tab.sounds;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.ui.Slider;
import com.github.argon.sos.mod.sdk.ui.Table;
import com.github.argon.sos.mod.sdk.ui.layout.Layout;
import com.github.argon.sos.mod.sdk.ui.layout.VerticalLayout;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.SoundsConfig;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
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
    private final static I18nTranslator i18n = ModModule.i18n().get(SoundsTab.class);

    private final Map<String, Slider> ambienceSoundSliders;
    private final Map<String, Slider> raceSoundSliders;

    public SoundsTab(
        MoreOptionsModel.Sounds model,
        int availableWidth,
        int availableHeight
    ) {
        super(model.getTitle(), model.getDefaultConfig(), availableWidth, availableHeight);
        SoundsConfig soundsConfig = model.getConfig();
        this.ambienceSoundSliders = UiMapper.toSliders(soundsConfig.getAmbience());
        this.raceSoundSliders = UiMapper.toSliders(soundsConfig.getRace());

        GHeader ambienceSoundsHeader = new GHeader(i18n.t("SoundsTab.header.ambienceSounds.name"));
        ambienceSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.ambienceSounds.desc"));

        GHeader roomSoundsHeader = new GHeader(i18n.t("SoundsTab.header.roomSounds.name"));
        roomSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.roomSounds.desc"));

        GHeader raceSoundsHeader = new GHeader(i18n.t("SoundsTab.header.raceSounds.name"));
        raceSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.raceSounds.desc"));

        Map<String, Slider> ambience = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> !stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Slider> room = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));




        int tableHeight = (availableHeight
            - ambienceSoundsHeader.body().height()
            - roomSoundsHeader.body().height()
            - raceSoundsHeader.body().height()
            - 30) / 3;

        Layout.vertical(tableHeight)
            .addDownC(0, ambienceSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<Integer>builder()
                .rows(UiMapper.toLabeledColumnRows(ambience, i18n))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(this);

        Layout.vertical(tableHeight)
            .addDownC(20, raceSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<String>builder()
                .rows(UiMapper.toLabeledColumnRows(raceSoundSliders, i18n))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(this);

        Layout.vertical(tableHeight)
            .addDownC(20, roomSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<String>builder()
                .rows(ModModule.uiMapper().toRoomSoundLabeledColumnRows(room))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .displaySearch(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(this);
    }

    @Override
    public SoundsConfig getValue() {
        return SoundsConfig.builder()
            .ambience(getAmbienceSoundConfig())
            .race(getRaceSoundConfig())
            .build();
    }

    private Map<String, Range> getAmbienceSoundConfig() {
        return ambienceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                slider -> Range.fromSlider(slider.getValue())));
    }

    private Map<String, Range> getRaceSoundConfig() {
        return raceSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                slider -> Range.fromSlider(slider.getValue())));
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

        soundsConfig.getRace().forEach((key, range) -> {
            if (raceSoundSliders.containsKey(key)) {
                raceSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected SoundsTab element() {
        return this;
    }
}
