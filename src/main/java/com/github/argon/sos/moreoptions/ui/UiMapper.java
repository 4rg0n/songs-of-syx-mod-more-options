package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Label;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.i18n.I18n;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import init.race.Race;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UiMapper {

    @Getter(lazy = true)
    private final static UiMapper instance = new UiMapper();

    private final GameApis gameApis = GameApis.getInstance();

    public Map<String, List<RacesPanel.Entry>> toRacePanelEntries(Set<MoreOptionsV2Config.RacesConfig.Liking> raceLikings) {
        Map<String, List<RacesPanel.Entry>> entries = new HashMap<>();

        for (MoreOptionsV2Config.RacesConfig.Liking raceLiking : raceLikings) {
            RacesPanel.Entry entry = toRacePanelEntry(raceLiking);
            String raceKey = entry.getRace().info.name.toString();
            entries.putIfAbsent(raceKey, new ArrayList<>());
            entries.get(raceKey).add(entry);
        }

        return entries;
    }

    public RacesPanel.Entry toRacePanelEntry(MoreOptionsV2Config.RacesConfig.Liking liking) {
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

    public List<BoostersPanel.Entry> toBoosterPanelEntries(Map<String, MoreOptionsV2Config.Range> boosterRanges) {
        return boosterRanges.entrySet().stream()
            .map(entry -> toBoosterPanelEntry(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public BoostersPanel.Entry toBoosterPanelEntry(String key, MoreOptionsV2Config.Range boosterRange) {
        return BoostersPanel.Entry.builder()
                .key(key)
                .range(boosterRange)
                .boosters(gameApis.booster().get(key))
                .cat(gameApis.booster().getCat(key))
                .build();
    }

    public static Map<String, List<BoostersPanel.Entry>> toBoosterPanelEntriesCategorized(List<BoostersPanel.Entry> boosterEntries) {
        Map<String, List<BoostersPanel.Entry>> groupedBoosterEntries = new HashMap<>();

        for (BoostersPanel.Entry entry : boosterEntries) {
            if (entry.getCat() != null && entry.getCat().name != null) {
                String catName = entry.getCat().name.toString();
                // Check if the map already has a list for this cat name
                if (!groupedBoosterEntries.containsKey(catName)) {
                    // If not, create a new list and add it to the map
                    groupedBoosterEntries.put(catName, new ArrayList<>());
                }
                // Add the entry to the appropriate list
                groupedBoosterEntries.get(catName).add(entry);
            }
        }

        return groupedBoosterEntries;
    }

    public static Map<String, Slider> toSliders(Map<String, MoreOptionsV2Config.Range> slidersConfig) {
        return slidersConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            config -> Slider.SliderBuilder
                .fromRange(config.getValue())
                .input(true)
                .width(300)
                .build()));
    }

    public static <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toLabeledColumnRows(Map<String, Element> elements, I18n i18n) {
        return elements.entrySet().stream().map(entry -> {
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
        }).collect(Collectors.toList());
    }

    public static Map<String, Checkbox> toCheckboxes(Map<String, Boolean> eventConfig) {
        return eventConfig.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> new Checkbox(entry.getValue())));
    }

    public static Slider.ValueDisplay toValueDisplay(MoreOptionsV2Config.Range.DisplayMode displayMode) {
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
}
