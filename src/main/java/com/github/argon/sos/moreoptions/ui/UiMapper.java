package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Label;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.panel.boosters.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.races.RacesPanel;
import game.faction.Faction;
import init.race.Race;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class UiMapper {

    private final static Logger log = Loggers.getLogger(UiMapper.class);

    @Getter(lazy = true)
    private final static UiMapper instance = new UiMapper();

    private final GameApis gameApis = GameApis.getInstance();

    public Map<String, List<RacesPanel.Entry>> toRacePanelEntries(Set<RacesConfig.Liking> raceLikings) {
        Map<String, List<RacesPanel.Entry>> entries = new HashMap<>();

        for (RacesConfig.Liking raceLiking : raceLikings) {
            RacesPanel.Entry entry = toRacePanelEntry(raceLiking);
            String raceKey = entry.getRace().info.name.toString();
            entries.putIfAbsent(raceKey, new ArrayList<>());
            entries.get(raceKey).add(entry);
        }

        return entries;
    }

    public RacesPanel.Entry toRacePanelEntry(RacesConfig.Liking liking) {
        Race race = gameApis.race().getRace(liking.getRace()).orElse(null);
        Race otherRace = gameApis.race().getRace(liking.getOtherRace()).orElse(null);

        return RacesPanel.Entry.builder()
            .raceKey(liking.getRace())
            .otherRaceKey(liking.getOtherRace())
            .race(race)
            .otherRace(otherRace)
            .range(liking.getRange())
            .build();
    }

    public Map<Faction, List<BoostersPanel.Entry>> toBoosterPanelEntries(BoostersConfig boostersConfig) {
        Map<Faction, List<BoostersPanel.Entry>> factionBoosters = new HashMap<>();
        // npc factions
        for (Map.Entry<String, Map<String, BoostersConfig.Booster>> entry : boostersConfig.getFaction().entrySet()) {
            String factionName = entry.getKey();
            Faction faction = gameApis.faction().getByName(factionName);

            if (faction == null) {
                log.debug("No faction for name '%s' found in game", factionName);
                continue;
            }

            factionBoosters.put(
                faction,
                entry.getValue().values().stream()
                    .map(this::toBoosterPanelEntry)
                    .collect(Collectors.toList()));
        }

        // add player faction
        factionBoosters.put(
            gameApis.faction().getPlayer(),
            boostersConfig.getPlayer().values().stream()
                .map(this::toBoosterPanelEntry)
                .collect(Collectors.toList()));

        return factionBoosters;
    }

    @Nullable
    public BoostersPanel.Entry toBoosterPanelEntry(BoostersConfig.Booster booster) {
        BoostersPanel.Entry.EntryBuilder entryBuilder = BoostersPanel.Entry.builder()
            .key(booster.getKey())
            .range(booster.getRange())
            .boosters(gameApis.booster().get(booster.getKey()))
            .cat(gameApis.booster().getCat(booster.getKey()));

        return entryBuilder.build();
    }

    public static Map<String, List<BoostersPanel.Entry>> toBoosterPanelEntriesCategorized(List<BoostersPanel.Entry> boosterEntries) {
        return boosterEntries.stream()
            .collect(groupingBy(entry -> entry.getCat().name.toString()));
    }

    public static Map<String, Slider> toSliders(Map<String, Range> slidersConfig) {
        return slidersConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> Slider.SliderBuilder
                .fromRange(config.getValue())
                .lockScroll(true)
                .input(true)
                .width(300)
                .build()));
    }

    public static Slider toSlider(Range range) {
        return Slider.SliderBuilder
            .fromRange(range)
            .lockScroll(true)
            .input(true)
            .width(300)
            .build();
    }

    public static <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toLabeledColumnRows(Map<String, Element> elements, I18n i18n) {
        return elements.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                // label with element
                return ColumnRow.<Value>builder()
                    .column(Label.builder()
                        .name(i18n.n(key))
                        .description(i18n.dn(key))
                        .maxWidth(300)
                        .build())
                    .column(element)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static Map<String, Checkbox> toCheckboxes(Map<String, Boolean> eventConfig) {
        return eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new Checkbox(entry.getValue())));
    }

    public static Slider.ValueDisplay toValueDisplay(Range.DisplayMode displayMode) {
        switch (displayMode) {
            case PERCENTAGE:
                return Slider.ValueDisplay.PERCENTAGE;
            case ABSOLUTE:
                return Slider.ValueDisplay.ABSOLUTE;
            default:
            case NONE:
                return Slider.ValueDisplay.NONE;
        }
    }

    public static Range.ApplyMode toApplyMode(Slider.ValueDisplay valueDisplay) {
        switch (valueDisplay) {
            case ABSOLUTE:
                return Range.ApplyMode.ADD;
            case PERCENTAGE:
                return Range.ApplyMode.PERCENT;
            case NONE:
            default:
                return Range.ApplyMode.MULTI;
        }
    }

    public static Range.DisplayMode toDisplayMode(Slider.ValueDisplay valueDisplay) {
        switch (valueDisplay) {
            case PERCENTAGE:
                return Range.DisplayMode.PERCENTAGE;
            case ABSOLUTE:
                return Range.DisplayMode.ABSOLUTE;
            default:
            case NONE:
                return Range.DisplayMode.NONE;
        }
    }
}
