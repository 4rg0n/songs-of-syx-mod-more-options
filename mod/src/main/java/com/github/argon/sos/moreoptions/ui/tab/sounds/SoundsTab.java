package com.github.argon.sos.moreoptions.ui.tab.sounds;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.ui.layout.Layout;
import com.github.argon.sos.mod.sdk.ui.layout.VerticalLayout;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.SoundsConfig;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
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
    private final Map<String, Slider> soundSliders;
    private final Map<String, Slider> animalSoundSliders;
    private final Map<String, Slider> roomWorkSoundSliders;

    public SoundsTab(
        String title,
        MoreOptionsModel.Sounds model,
        int availableWidth,
        int availableHeight
    ) {
        super(title, model.getDefaultConfig(), availableWidth, availableHeight);

        SoundsConfig soundsConfig = model.getConfig();
        this.ambienceSoundSliders = UiMapper.toSliders(soundsConfig.getAmbience());
        this.raceSoundSliders = UiMapper.toSliders(soundsConfig.getRace());
        this.soundSliders = UiMapper.toSliders(soundsConfig.getSounds());
        this.animalSoundSliders = UiMapper.toSliders(soundsConfig.getAnimal());
        this.roomWorkSoundSliders = UiMapper.toSliders(soundsConfig.getRoom());

        GHeader ambienceSoundsHeader = new GHeader(i18n.t("SoundsTab.header.ambienceSounds.name"));
        ambienceSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.ambienceSounds.desc"));

        GHeader raceSoundsHeader = new GHeader(i18n.t("SoundsTab.header.raceSounds.name"));
        raceSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.raceSounds.desc"));

        GHeader settlementSoundsHeader = new GHeader(i18n.t("SoundsTab.header.settlementSounds.name"));
        settlementSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.settlementSounds.desc"));

        GHeader animalSoundsHeader = new GHeader(i18n.t("SoundsTab.header.animalSounds.name"));
        animalSoundsHeader.hoverInfoSet(i18n.d("SoundsTab.header.animalSounds.desc"));

        Map<String, Slider> ambience = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> !stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Slider> roomAmbience = ambienceSoundSliders.entrySet().stream()
            .filter(stringSliderEntry -> stringSliderEntry.getKey().contains("ROOM_"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Switcher<String> menu = Switcher.<String>builder()
            .aktiveKey("ambienceAndRace")
            .highlight(true)
            .menu(ButtonMenu.<String>builder()
                .margin(10)
                .spacer(true)
                .maxWidth(availableWidth)
                .horizontal(true)
                .button("ambienceAndRace", new Button(
                    i18n.t("SoundsTab.tab.ambienceAndRaceSounds.name"),
                    i18n.t("SoundsTab.tab.ambienceAndRaceSounds.desc")
                ))
                .button("settlementAndAnimal", new Button(
                    i18n.t("SoundsTab.tab.settlementAndAnimal.name"),
                    i18n.t("SoundsTab.tab.settlementAndAnimal.desc")
                ))
                .button("roomWork", new Button(
                    i18n.t("SoundsTab.tab.roomWork.name"),
                    i18n.t("SoundsTab.tab.roomWork.desc")
                ))
                .button("roomAmbience", new Button(
                    i18n.t("SoundsTab.tab.roomAmbience.name"),
                    i18n.t("SoundsTab.tab.roomAmbience.desc")
                ))
                .build())
            .build();

        int tableHeight = (availableHeight
            - menu.body().height());

        GuiSection ambienceAndRaceTab = new GuiSection();
        GuiSection settlementAndAnimalTab = new GuiSection();
        GuiSection roomAmbienceTab = new GuiSection();
        GuiSection roomWorkTab = new GuiSection();

        // Ambience
        Layout.vertical(tableHeight / 2)
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
            .build(ambienceAndRaceTab);

        // Race
        Layout.vertical(tableHeight / 2)
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
            .build(ambienceAndRaceTab);

        // Settlement
        Layout.vertical(tableHeight / 2)
            .addDownC(0, settlementSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<Integer>builder()
                .rows(UiMapper.toLabeledColumnRows(soundSliders, i18n))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(settlementAndAnimalTab);

        // Animal
        Layout.vertical(tableHeight / 2)
            .addDownC(20, animalSoundsHeader)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<String>builder()
                .rows(ModModule.uiMapper().toAnimalSoundLabeledColumnRows(animalSoundSliders))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(settlementAndAnimalTab);

        // Room
        Layout.vertical(tableHeight)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<String>builder()
                .rows(ModModule.uiMapper().toRoomSoundLabeledColumnRows(roomWorkSoundSliders))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .displaySearch(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(roomWorkTab);

        // Room
        Layout.vertical(tableHeight)
            .addDownC(5, new VerticalLayout.Scalable(150, height -> Table.<String>builder()
                .rows(ModModule.uiMapper().toRoomSoundLabeledColumnRows(roomAmbience))
                .rowPadding(5)
                .columnMargin(5)
                .highlight(true)
                .scrollable(true)
                .displaySearch(true)
                .backgroundColor(COLOR.WHITE10)
                .displayHeight(height)
                .build()))
            .build(roomAmbienceTab);

        Tabulator<String, RENDEROBJ, Void> tabulator = Tabulator.<String, RENDEROBJ, Void>builder()
            .tabMenu(menu)
            .center(true)
            .tabs(Maps.of(
                "ambienceAndRace", ambienceAndRaceTab,
                "settlementAndAnimal", settlementAndAnimalTab,
                "roomWork", roomWorkTab,
                "roomAmbience", roomAmbienceTab
            ))
            .build();

        addDownC(0, menu);
        addDownC(15, tabulator);
    }

    @Override
    public SoundsConfig getValue() {
        return SoundsConfig.builder()
            .ambience(getAmbienceSoundConfig())
            .race(getRaceSoundConfig())
            .sounds(getSettlementSoundConfig())
            .animal(getAnimalSoundConfig())
            .room(getRoomWorkSoundConfig())
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

    private Map<String, Range> getAnimalSoundConfig() {
        return animalSoundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                slider -> Range.fromSlider(slider.getValue())));
    }

    private Map<String, Range> getSettlementSoundConfig() {
        return soundSliders.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                slider -> Range.fromSlider(slider.getValue())));
    }

    private Map<String, Range> getRoomWorkSoundConfig() {
        return roomWorkSoundSliders.entrySet().stream()
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

        soundsConfig.getAnimal().forEach((key, range) -> {
            if (animalSoundSliders.containsKey(key)) {
                animalSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        soundsConfig.getSounds().forEach((key, range) -> {
            if (soundSliders.containsKey(key)) {
                soundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });

        soundsConfig.getRace().forEach((key, range) -> {
            if (roomWorkSoundSliders.containsKey(key)) {
                roomWorkSoundSliders.get(key).setValue(range.getValue());
            } else {
                log.warn("No slider with key %s found in UI", key);
            }
        });
    }

    protected SoundsTab element() {
        return this;
    }
}
