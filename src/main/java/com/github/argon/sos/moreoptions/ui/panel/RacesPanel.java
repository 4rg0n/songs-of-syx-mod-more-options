package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.element.SliderBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import com.github.argon.sos.moreoptions.util.UiUtil;
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
public class RacesPanel extends GuiSection implements Valuable<MoreOptionsV2Config.RacesConfig, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);

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

    public RacesPanel(Map<String, List<Entry>> raceEntries) {
        // Race Likings rows for table
        Map<String, List<ColumnRow<Integer>>> raceLikingsRowMap = raceEntries.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
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
                BuildResult<Slider, Slider> likingsSliderResult = SliderBuilder.builder().definition(SliderBuilder.Definition
                    .fromRange(range)
                    .maxWidth(300)
                    .threshold(0, COLOR.RED100.shade(0.5d))
                    .threshold((int) (0.25 * range.getMax()), COLOR.YELLOW100.shade(0.5d))
                    .threshold((int) (0.50 * range.getMax()), COLOR.ORANGE100.shade(0.5d))
                    .threshold((int) (0.75 * range.getMax()), COLOR.GREEN100.shade(0.5d))
                    .build()
                ).build();

                Slider slider = likingsSliderResult.getResult();
                likingsSliders.put(key(race, otherRace), slider);
                List<GuiSection> columns = Lists.of(raceIcon, slider, otherRaceIcon);

                return ColumnRow.<Integer>builder()
                    .columns(columns)
                    .searchTerm(term(race, otherRace))
                    .highlightable(true)
                    .columns(columns)
                    .build();
            }).collect(Collectors.toList())));

        // Race Likings table with search
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder("Search");
        Table<Integer> raceLikingsTable = Table.<Integer>builder()
            .evenOdd(true)
            .scrollable(true)
            .search(searchInput)
            .rowPadding(3)
            .rowsCategorized(raceLikingsRowMap)
            .displayHeight(400)
            .build();

        // menu with buttons
        this.folderButton = new Button("Folder", "Opens the folder containing the races configs.");
        this.fileButton = new Button("File", "Opens the current used race config file.");
        this.loadButton = new Button("Load", "Load races config from another save game.");
        this.exportButton = new Button("Export", "Copy current races config from ui into clipboard.");
        this.importButton = new Button("Import", "Applies races config from clipboard to ui.");
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
        GText raceLikingsHeader = new GText(UI.FONT().H2,"Race likings");
        searchBar.addRightC(0, raceLikingsHeader);
        searchBar.addRightC(20, new GInput(searchInput));
        searchBar.hoverInfoSet("Races config is bound to the save game. " +
            "A new game will start with vanilla game settings. " +
            "You can load settings from other saves though.");

        addDownC(20, buttonMenu);
        addDownC(20, searchBar);
        addDownC(20, raceLikingsTable);
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
