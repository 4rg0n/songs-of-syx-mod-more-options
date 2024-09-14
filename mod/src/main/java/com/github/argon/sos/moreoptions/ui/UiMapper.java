package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.mod.sdk.game.api.GameApis;
import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.Label;
import com.github.argon.sos.mod.sdk.game.util.UiUtil;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.game.api.GameBoosterApi;
import com.github.argon.sos.moreoptions.ui.tab.boosters.BoostersTab;
import com.github.argon.sos.moreoptions.ui.tab.races.RacesTab;
import game.faction.Faction;
import init.race.Race;
import init.sprite.SPRITES;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.room.main.RoomBlueprintImp;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.SPRITE;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class UiMapper {

    private final static Logger log = Loggers.getLogger(UiMapper.class);

    private final GameApis gameApis;
    private final GameBoosterApi gameBoosterApi;

    public Map<String, List<RacesTab.Entry>> toRacePanelEntries(Set<RacesConfig.Liking> raceLikings) {
        Map<String, List<RacesTab.Entry>> entries = new HashMap<>();

        for (RacesConfig.Liking raceLiking : raceLikings) {
            RacesTab.Entry entry = toRacePanelEntry(raceLiking);
            String raceKey = entry.getRace().info.name.toString();
            entries.putIfAbsent(raceKey, new ArrayList<>());
            entries.get(raceKey).add(entry);
        }

        return entries;
    }

    public RacesTab.Entry toRacePanelEntry(RacesConfig.Liking liking) {
        Race race = gameApis.race().getRace(liking.getRace()).orElse(null);
        Race otherRace = gameApis.race().getRace(liking.getOtherRace()).orElse(null);

        return RacesTab.Entry.builder()
            .raceKey(liking.getRace())
            .otherRaceKey(liking.getOtherRace())
            .race(race)
            .otherRace(otherRace)
            .range(liking.getRange())
            .build();
    }

    public Map<Faction, List<BoostersTab.Entry>> toBoosterPanelEntries(BoostersConfig boostersConfig) {
        Map<Faction, List<BoostersTab.Entry>> factionBoosters = new HashMap<>();
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
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        // add player faction
        factionBoosters.put(
            gameApis.faction().getPlayer(),
            boostersConfig.getPlayer().values().stream()
                .map(this::toBoosterPanelEntry)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        return factionBoosters;
    }

    @Nullable
    public BoostersTab.Entry toBoosterPanelEntry(BoostersConfig.Booster booster) {
        return gameBoosterApi.get(booster.getKey()).map(boosters -> BoostersTab.Entry.builder()
            .key(booster.getKey())
            .range(booster.getRange())
            .boosters(boosters)
            .cat(gameBoosterApi.getCat(booster.getKey()))
            .build()
        ).orElse(null);
    }

    public static Map<String, List<BoostersTab.Entry>> toBoosterPanelEntriesCategorized(List<BoostersTab.Entry> boosterEntries) {
        return boosterEntries.stream()
            .collect(groupingBy(entry -> entry.getCat().name.toString()));
    }

    public <Value, Element extends RENDEROBJ> List<ColumnRow<Value>> toRoomSoundLabeledColumnRows(Map<String, Element> elements) {
        return elements.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                String key = entry.getKey();
                Element element = entry.getValue();

                String name = key;
                SPRITE sprite = SPRITES.icons().m.clear_structure;
                Optional<RoomBlueprintImp> bySound = gameApis.rooms().getBySound(key);

                if (bySound.isPresent()) {
                    name = bySound.get().info.name.toString();
                    sprite = bySound.get().iconBig().medium;
                }

                // label with element
                return ColumnRow.<Value>builder()
                    .column(Label.builder()
                        .name(name)
                        .build())
                    .column(UiUtil.toRender(sprite))
                    .column(element)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
