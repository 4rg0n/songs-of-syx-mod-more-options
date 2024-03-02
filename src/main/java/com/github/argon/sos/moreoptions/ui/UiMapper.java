package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.ui.panel.BoostersPanel;
import com.github.argon.sos.moreoptions.ui.panel.RacesPanel;
import init.race.Race;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public abstract class UiMapper {
    private final GameApis gameApis = GameApis.getInstance();

    public List<RacesPanel.Entry> mapToRacePanelEntries(List<MoreOptionsV2Config.Races.Liking> raceLikings) {
        return raceLikings.stream().map(liking -> {
            Race race = gameApis.races().getRace(liking.getRace()).orElse(null);
            Race otherRace = gameApis.races().getRace(liking.getOtherRace()).orElse(null);

            return RacesPanel.Entry.builder()
                .raceKey(liking.getRace())
                .otherRaceKey(liking.getOtherRace())
                .race(race)
                .otherRace(otherRace)
                .range(liking.getRange())
                .build();
        }).collect(Collectors.toList());
    }

    @NotNull
    public List<BoostersPanel.Entry> mapToBoosterPanelEntries(Map<String, MoreOptionsV2Config.Range> boosterRanges) {
        return boosterRanges.entrySet().stream()
            .map(entry -> BoostersPanel.Entry.builder()
                .key(entry.getKey())
                .range(entry.getValue())
                .boosters(gameApis.booster().get(entry.getKey()))
                .cat(gameApis.booster().getCat(entry.getKey()))
                .build())
            .collect(Collectors.toList());
    }
}
