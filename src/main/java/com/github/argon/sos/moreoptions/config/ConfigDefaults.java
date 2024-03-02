package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigDefaults {

    private final static Logger log = Loggers.getLogger(ConfigDefaults.class);

    @Getter(lazy = true)
    private final static ConfigDefaults instance = new ConfigDefaults(
        GameApis.getInstance()
    );


    private final GameApis gameApis;

    public static MoreOptionsV2Config.Range boosterAdd() {
        return MoreOptionsV2Config.Range.builder()
            .value(0)
            .min(0)
            .max(10000)
            .applyMode(MoreOptionsV2Config.Range.ApplyMode.ADD)
            .displayMode(MoreOptionsV2Config.Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static MoreOptionsV2Config.Range boosterMulti() {
        return MoreOptionsV2Config.Range.builder()
            .value(100)
            .min(1)
            .max(10000)
            .applyMode(MoreOptionsV2Config.Range.ApplyMode.MULTI)
            .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
            .build();
    }

    public static MoreOptionsV2Config.Metrics metrics() {
        return MoreOptionsV2Config.Metrics.builder().build();
    }

    public static MoreOptionsV2Config.Range raceLiking() {
        return MoreOptionsV2Config.Range.builder()
            .min(0)
            .max(100)
            .value(0)
            .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
            .applyMode(MoreOptionsV2Config.Range.ApplyMode.ADD)
            .build();
    }

    public static MoreOptionsV2Config.Range metricCollectionRate() {
        return MoreOptionsV2Config.Range.builder()
            .min(5)
            .value(15)
            .max(600)
            .applyMode(MoreOptionsV2Config.Range.ApplyMode.ADD)
            .displayMode(MoreOptionsV2Config.Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static MoreOptionsV2Config.Range metricExportRate() {
        return MoreOptionsV2Config.Range.builder()
            .min(5)
            .value(15)
            .max(600)
            .applyMode(MoreOptionsV2Config.Range.ApplyMode.ADD)
            .displayMode(MoreOptionsV2Config.Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static MoreOptionsV2Config.Range weather() {
        return MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build();
    }

    public static MoreOptionsV2Config.Range sound() {
        return MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build();
    }

    public static MoreOptionsV2Config.Range eventChance() {
        return MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(10000)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build();
    }

    public MoreOptionsV2Config newDefaultConfig() {
        // Boosters
        Map<String, MoreOptionsV2Config.Range> multiBoosters = gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.boosterMulti()));

        // Weather
        Map<String, MoreOptionsV2Config.Range> weatherRanges = gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.weather()));

        // Sounds Ambience
        Map<String, MoreOptionsV2Config.Range> ambienceSounds = gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Room
        Map<String, MoreOptionsV2Config.Range> roomSounds = gameApis.sounds().getRoomSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Settlement
        Map<String, MoreOptionsV2Config.Range> settlementSounds = gameApis.sounds().getSettlementSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Events Chance
        Map<String, MoreOptionsV2Config.Range> eventChances = gameApis.events().getEventsChance().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> ConfigDefaults.eventChance()));

        // Events Settlement
        Map<String, Boolean> settlementEvents = gameApis.events().getSettlementEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        // Event World
        Map<String, Boolean> worldEvents = gameApis.events().getWorldEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        // Metrics
        MoreOptionsV2Config.Metrics metrics = ConfigDefaults.metrics();
        List<String> availableStats = gameApis.stats().getAvailableStatKeys();
        metrics.setStats(availableStats);

        // Races
        List<Race> racesAll = gameApis.races().getAll();
        List<Race> otherRacesAll = new ArrayList<>(racesAll);
        List<MoreOptionsV2Config.Races.Liking> raceLikings = new ArrayList<>();
        for (Race race : racesAll) {
            for (Race otherRace : otherRacesAll) {
                double racePref = race.pref().race(otherRace);
                int value = MathUtil.fromPercentage(racePref);

                MoreOptionsV2Config.Range range = raceLiking();
                range.setValue(value);

                raceLikings.add(MoreOptionsV2Config.Races.Liking.builder()
                    .race(race.key)
                    .otherRace(otherRace.key)
                    .range(range)
                    .build());
            }
        }
        MoreOptionsV2Config.Races races = MoreOptionsV2Config.Races.builder()
            .likings(raceLikings)
            .build();

        return MoreOptionsV2Config.builder()
            .events(MoreOptionsV2Config.Events.builder()
                .world(worldEvents)
                .settlement(settlementEvents)
                .chance(eventChances)
                .build())
            .sounds(MoreOptionsV2Config.Sounds.builder()
                .ambience(ambienceSounds)
                .settlement(settlementSounds)
                .room(roomSounds)
                .build())
            .weather(weatherRanges)
            .boosters(multiBoosters)
            .metrics(metrics)
            .races(races)
            .build();
    }
}
