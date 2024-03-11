package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.game.ui.layout.Layout;
import com.github.argon.sos.moreoptions.game.ui.layout.VerticalLayout;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import com.github.argon.sos.moreoptions.game.util.UiUtil;
import init.race.Race;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GInput;
import util.gui.misc.GText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contains race likings adjustable via sliders
 */
public class RacesPanel extends AbstractPanel<MoreOptionsV2Config.RacesConfig, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);

    private final static I18n i18n = I18n.get(RacesPanel.class);

    private final Map<String, Slider> likingsSliders = new HashMap<>();
    private final static String RACE_SEPARATOR = "~";

    @Getter
    private final Button folderButton;
    @Getter
    private final Button fileButton;
    @Getter
    private final Button loadButton;
    @Getter
    private final Button exportButton;
    @Getter
    private final Button importButton;

    public RacesPanel(
        String title,
        Map<String, List<Entry>> raceEntries,
        MoreOptionsV2Config.RacesConfig defaultConfig,
        int availableWidth,
        int availableHeight
    ) {
        super(title, defaultConfig);
        // Race Likings rows for table
        Map<String, List<ColumnRow<Integer>>> raceLikingsRowMap = raceEntries.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            mapEntry -> mapEntry.getValue().stream().map(entry -> {
                Race race = entry.getRace();
                Race otherRace = entry.getOtherRace();
                MoreOptionsV2Config.Range range = entry.getRange();

                // Race icons
                GuiSection raceIcon = UiUtil.toGuiSection(race.appearance().icon);
                raceIcon.hoverInfoSet(race.info.name);
                GuiSection otherRaceIcon = UiUtil.toGuiSection(otherRace.appearance().icon);
                otherRaceIcon.hoverInfoSet(otherRace.info.name);

                // Likings Slider
                Slider likingsSlider = Slider.SliderBuilder
                    .fromRange(range)
                    .width(300)
                    .input(true)
                    .lockScroll(true)
                    .threshold(0, COLOR.RED100.shade(0.5d))
                    .threshold((int) (0.25 * range.getMax()), COLOR.YELLOW100.shade(0.5d))
                    .threshold((int) (0.50 * range.getMax()), COLOR.ORANGE100.shade(0.5d))
                    .threshold((int) (0.75 * range.getMax()), COLOR.GREEN100.shade(0.5d))
                    .build();

                likingsSliders.put(key(race, otherRace), likingsSlider);
                List<GuiSection> columns = Lists.of(raceIcon, likingsSlider, otherRaceIcon);

                return ColumnRow.<Integer>builder()
                    .searchTerm(term(race, otherRace))
                    .highlightable(true)
                    .columns(columns)
                    .build();
            }).collect(Collectors.toList())));

        // Race Likings table with search
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.t("RacesPanel.search.input.name"));
        GInput search = new GInput(searchInput);

        // menu with buttons
        this.folderButton = new Button(i18n.t("RacesPanel.button.folder.name"), i18n.t("RacesPanel.button.folder.desc"));
        this.fileButton = new Button(i18n.t("RacesPanel.button.file.name"), i18n.t("RacesPanel.button.file.desc"));
        this.loadButton = new Button(i18n.t("RacesPanel.button.load.name"), i18n.t("RacesPanel.button.load.desc"));
        this.exportButton = new Button(i18n.t("RacesPanel.button.export.name"), i18n.t("RacesPanel.button.export.desc"));
        this.importButton = new Button(i18n.t("RacesPanel.button.import.name"), i18n.t("RacesPanel.button.import.desc"));
        ButtonMenu<String> buttonMenu = ButtonMenu.<String>builder()
            .buttons(Maps.ofLinked(
                "load", loadButton,
            "folder", folderButton,
            "file", fileButton,
            "export", exportButton,
            "import", importButton))
            .horizontal(true)
            .spacer(true)
            .margin(21)
            .build();

        // header with info text
        GuiSection searchBar = new GuiSection();
        GText raceLikingsHeader = new GText(UI.FONT().H2, i18n.t("RacesPanel.search.header.name"));
        searchBar.addRightC(0, raceLikingsHeader);
        searchBar.addRightC(20, search);
        searchBar.hoverInfoSet(i18n.t("RacesPanel.search.header.desc"));

        Layout.vertical(availableHeight)
            .addDownC(0, buttonMenu)
            .addDownC(20, searchBar)
            .addDownC(20, new VerticalLayout.Scalable(300, height ->  Table.<Integer>builder()
                .evenOdd(true)
                .scrollable(true)
                .search(searchInput)
                .rowPadding(5)
                .rowsCategorized(raceLikingsRowMap)
                .displayHeight(height)
                .build()))
            .build(this);
    }

    @Override
    public MoreOptionsV2Config.RacesConfig getValue() {
        Set<MoreOptionsV2Config.RacesConfig.Liking> likings = likingsSliders.entrySet().stream().map(entry -> {
            String[] split = entry.getKey().split(RACE_SEPARATOR);
            String race = split[0];
            String otherRace = split[1];

            return MoreOptionsV2Config.RacesConfig.Liking.builder()
                .race(race)
                .otherRace(otherRace)
                .range(MoreOptionsV2Config.Range.fromSlider(entry.getValue()))
                .build();
        }).collect(Collectors.toSet());

        return MoreOptionsV2Config.RacesConfig.builder()
            .likings(likings)
            .build();
    }

    @Override
    public void setValue(MoreOptionsV2Config.RacesConfig config) {
        config.getLikings().forEach(liking -> {
            String key = key(liking);
            Slider slider = likingsSliders.get(key);

            if (slider != null) {
                slider.setValue(liking.getRange().getValue());
            }
        });
    }

    private String key(MoreOptionsV2Config.RacesConfig.Liking liking) {
        return liking.getRace() + RACE_SEPARATOR + liking.getOtherRace();
    }

    private String key(Race race, Race otherRace) {
        return race.key + RACE_SEPARATOR + otherRace.key;
    }

    private String term(Race race, Race otherRace) {
        return race.info.name + RACE_SEPARATOR + otherRace.info.name;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private String raceKey;
        private String otherRaceKey;
        private Race race;
        private Race otherRace;
        private MoreOptionsV2Config.Range range;
    }
}
