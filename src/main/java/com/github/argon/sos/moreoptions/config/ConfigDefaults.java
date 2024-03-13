package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.config.MoreOptionsV2Config.*;

/**
 * Provides default configuration partially gathered from the game.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigDefaults {

    private final static Logger log = Loggers.getLogger(ConfigDefaults.class);

    public final static Level CONFIG_DEFAULT_LOG_LEVEL = Level.INFO;

    @Getter(lazy = true)
    private final static ConfigDefaults instance = new ConfigDefaults(
        GameApis.getInstance()
    );

    private final GameApis gameApis;

    public MoreOptionsV2Config newDefaultConfig() {
        log.debug("Creating new default config");
        // Boosters
        Map<String, Range> multiBoosters = gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.boosterMulti()));

        // Weather
        Map<String, Range> weatherRanges = gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.weather()));

        // Sounds Ambience
        Map<String, Range> ambienceSounds = gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Room
        Map<String, Range> roomSounds = gameApis.sounds().getRoomSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Settlement
        Map<String, Range> settlementSounds = gameApis.sounds().getSettlementSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Events Chance
        Map<String, Range> eventChances = gameApis.events().getEventsChance().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> ConfigDefaults.eventChance()));

        // Events Settlement
        Map<String, Boolean> settlementEvents = gameApis.events().getSettlementEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        // Event World
        Map<String, Boolean> worldEvents = gameApis.events().getWorldEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        // Metrics
        Metrics metrics = ConfigDefaults.metrics();
        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        metrics.setStats(availableStats);

        // Races
        List<Race> racesAll = gameApis.race().getAll();
        List<Race> otherRacesAll = new ArrayList<>(racesAll);
        Set<RacesConfig.Liking> raceLikings = new HashSet<>();
        for (Race race : racesAll) {
            for (Race otherRace : otherRacesAll) {
                double racePref = race.pref().race(otherRace);
                int value = MathUtil.fromPercentage(racePref);

                Range range = raceLiking();
                range.setValue(value);

                raceLikings.add(RacesConfig.Liking.builder()
                    .race(race.key)
                    .otherRace(otherRace.key)
                    .range(range)
                    .build());
            }
        }
        RacesConfig races = RacesConfig.builder()
            .likings(raceLikings)
            .build();

        MoreOptionsV2Config defaultConfig = builder()
            .events(Events.builder()
                .world(worldEvents)
                .settlement(settlementEvents)
                .chance(eventChances)
                .build())
            .sounds(Sounds.builder()
                .ambience(ambienceSounds)
                .settlement(settlementSounds)
                .room(roomSounds)
                .build())
            .weather(weatherRanges)
            .boosters(multiBoosters)
            .metrics(metrics)
            .races(races)
            .build();

        log.trace("Default config: %s", defaultConfig);
        return defaultConfig;
    }

    public static Range boosterAdd() {
        return Range.builder()
            .value(0)
            .min(0)
            .max(10000)
            .applyMode(Range.ApplyMode.ADD)
            .displayMode(Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static Range boosterMulti() {
        return Range.builder()
            .value(100)
            .min(0)
            .max(10000)
            .applyMode(Range.ApplyMode.PERCENT)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .build();
    }

    public static Metrics metrics() {
        return Metrics.builder().build();
    }

    public static Range raceLiking() {
        return Range.builder()
            .min(0)
            .max(100)
            .value(0)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }

    public static Range metricCollectionRate() {
        return Range.builder()
            .min(5)
            .value(15)
            .max(600)
            .applyMode(Range.ApplyMode.ADD)
            .displayMode(Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static Range metricExportRate() {
        return Range.builder()
            .min(5)
            .value(15)
            .max(600)
            .applyMode(Range.ApplyMode.ADD)
            .displayMode(Range.DisplayMode.ABSOLUTE)
            .build();
    }

    public static Range weather() {
        return Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(Range.DisplayMode.PERCENTAGE)
                .build();
    }

    public static Range sound() {
        return Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(Range.DisplayMode.PERCENTAGE)
                .build();
    }

    public static Range eventChance() {
        return Range.builder()
                .value(100)
                .min(0)
                .max(10000)
                .displayMode(Range.DisplayMode.PERCENTAGE)
                .build();
    }
}
