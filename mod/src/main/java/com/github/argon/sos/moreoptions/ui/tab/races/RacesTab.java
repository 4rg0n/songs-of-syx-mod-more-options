package com.github.argon.sos.moreoptions.ui.tab.races;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.ui.*;
import com.github.argon.sos.mod.sdk.ui.layout.Layout;
import com.github.argon.sos.mod.sdk.ui.layout.VerticalLayout;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModel;
import com.github.argon.sos.moreoptions.ui.tab.AbstractConfigTab;
import init.race.Race;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.StringInputSprite;
import util.gui.misc.GText;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains race likings adjustable via sliders
 */
public class RacesTab extends AbstractConfigTab<RacesConfig, RacesTab> {

    private final static I18nTranslator i18n = ModModule.i18n().get(RacesTab.class);

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

    public RacesTab(
        MoreOptionsModel.Races model,
        int availableWidth,
        int availableHeight
    ) {
        super(model.getTitle(), model.getDefaultConfig(), availableWidth, availableHeight);
        Map<String, List<Entry>> raceEntries = model.getEntries();

        // Race Likings rows for table
        Map<String, List<ColumnRow<Integer>>> raceLikingsRowMap = raceEntries.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            mapEntry -> mapEntry.getValue().stream()
                .sorted(Comparator.comparing(entry -> entry.getOtherRace().info.name.toString()))
                .map(entry -> {
                    Race race = entry.getRace();
                    Race otherRace = entry.getOtherRace();
                    Range range = entry.getRange();

                    // Race icons
                    GuiSection raceIcon = UiUtil.toGuiSection(race.appearance().icon);
                    raceIcon.hoverInfoSet(race.info.name);
                    GuiSection otherRaceIcon = UiUtil.toGuiSection(otherRace.appearance().icon);
                    otherRaceIcon.hoverInfoSet(otherRace.info.name);

                    // Likings Slider
                    Slider likingsSlider = Slider.SliderBuilder
                        .fromRange(range)
                        .width(300)
                        .controls(true)
                        .input(true)
                        .lockScroll(true)
                        .threshold(0, COLOR.RED100.shade(0.5d))
                        .threshold((int) (0.25 * range.getMax()), COLOR.ORANGE100.shade(0.5d))
                        .threshold((int) (0.50 * range.getMax()), COLOR.YELLOW100.shade(0.5d))
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
        StringInputSprite searchInput = new StringInputSprite(16, UI.FONT().M).placeHolder(i18n.t("RacesTab.search.input.name"));
        Input search = new Input(searchInput);

        // menu with buttons
        this.folderButton = new Button(i18n.t("RacesTab.button.folder.name"), i18n.t("RacesTab.button.folder.desc"));
        this.fileButton = new Button(i18n.t("RacesTab.button.file.name"), i18n.t("RacesTab.button.file.desc"));
        this.loadButton = new Button(i18n.t("RacesTab.button.load.name"), i18n.t("RacesTab.button.load.desc"));
        this.exportButton = new Button(i18n.t("RacesTab.button.export.name"), i18n.t("RacesTab.button.export.desc"));
        this.importButton = new Button(i18n.t("RacesTab.button.import.name"), i18n.t("RacesTab.button.import.desc"));
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
            .maxWidth(availableWidth)
            .build();

        // header with info text
        GuiSection searchBar = new GuiSection();
        GText raceLikingsHeader = new GText(UI.FONT().H2, i18n.t("RacesTab.search.header.name"));
        searchBar.addRightC(0, raceLikingsHeader);
        searchBar.addRightC(20, search);
        searchBar.hoverInfoSet(i18n.t("RacesTab.search.header.desc"));

       Layout.vertical(availableHeight)
            .addDownC(0, buttonMenu)
            .addDownC(20, searchBar)
            .addDownC(20, new VerticalLayout.Scalable(300, height ->  Table.<Integer>builder()
                .evenOdd(true)
                .scrollable(true)
                .highlight(true)
                .search(searchInput)
                .rowPadding(5)
                .columnMargin(5)
                .backgroundColor(COLOR.WHITE10)
                .rowsCategorized(raceLikingsRowMap)
                .displayHeight(height)
                .build()))
            .build(this);
    }

    @Override
    public RacesConfig getValue() {
        Set<RacesConfig.Liking> likings = likingsSliders.entrySet().stream().map(entry -> {
            String[] split = entry.getKey().split(RACE_SEPARATOR);
            String race = split[0];
            String otherRace = split[1];

            return RacesConfig.Liking.builder()
                .race(race)
                .otherRace(otherRace)
                .range(Range.fromSlider(entry.getValue()))
                .build();
        }).collect(Collectors.toSet());

        return RacesConfig.builder()
            .likings(likings)
            .build();
    }

    @Override
    public void setValue(RacesConfig config) {
        config.getLikings().forEach(liking -> {
            String key = key(liking);
            Slider slider = likingsSliders.get(key);

            if (slider != null) {
                slider.setValue(liking.getRange().getValue());
            }
        });
    }

    private String key(RacesConfig.Liking liking) {
        return liking.getRace() + RACE_SEPARATOR + liking.getOtherRace();
    }

    private String key(Race race, Race otherRace) {
        return race.key + RACE_SEPARATOR + otherRace.key;
    }

    private String term(Race race, Race otherRace) {
        return race.info.name + RACE_SEPARATOR + otherRace.info.name;
    }

    protected RacesTab element() {
        return this;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class Entry {
        private String raceKey;
        private String otherRaceKey;
        private Race race;
        private Race otherRace;
        private Range range;
    }
}
