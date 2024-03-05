package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import init.race.Race;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UiMapper {

    @Getter(lazy = true)
    private final static UiMapper instance = new UiMapper();

    private final GameApis gameApis = GameApis.getInstance();

    public Map<String, List<RacesPanel.Entry>> mapToRacePanelEntries(Set<MoreOptionsV2Config.RacesConfig.Liking> raceLikings) {
        Map<String, List<RacesPanel.Entry>> entries = new HashMap<>();

        for (MoreOptionsV2Config.RacesConfig.Liking raceLiking : raceLikings) {
            RacesPanel.Entry entry = mapToRacePanelEntry(raceLiking);
            String raceKey = entry.getRace().info.name.toString();
            entries.putIfAbsent(raceKey, new ArrayList<>());
            entries.get(raceKey).add(entry);
        }

        return entries;
    }

    public RacesPanel.Entry mapToRacePanelEntry(MoreOptionsV2Config.RacesConfig.Liking liking) {
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

    public List<BoostersPanel.Entry> mapToBoosterPanelEntries(Map<String, MoreOptionsV2Config.Range> boosterRanges) {
        return boosterRanges.entrySet().stream()
            .map(entry ->mapToBoosterPanelEntry(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public BoostersPanel.Entry mapToBoosterPanelEntry(String key, MoreOptionsV2Config.Range boosterRange) {
        return BoostersPanel.Entry.builder()
                .key(key)
                .range(boosterRange)
                .boosters(gameApis.booster().get(key))
                .cat(gameApis.booster().getCat(key))
                .build();
    }
}
