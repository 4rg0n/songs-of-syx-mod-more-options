package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.faction.Faction;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.config.MoreOptionsV3Config.*;

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

    public MoreOptionsV3Config newConfig() {
        log.debug("Creating new default config");

        MoreOptionsV3Config defaultConfig = builder()
            .events(newEvents())
            .sounds(newSounds())
            .weather(newWeather())
            .boosters(newBoostersConfig())
            .metrics(newMetrics())
            .races(newRacesConfig())
            .build();

        log.trace("Default config: %s", defaultConfig);
        return defaultConfig;
    }

    public RacesConfig newRacesConfig() {
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

        return RacesConfig.builder()
            .likings(raceLikings)
            .build();
    }

    public Map<String, Set<BoostersConfig.Booster>> newBoosters(Collection<? extends Faction> factions) {
        Map<String, Set<BoostersConfig.Booster>> factionBoosters = new HashMap<>();
        for (Faction faction : factions) {
            Set<BoostersConfig.Booster> boosters = newBoosters(faction);
            factionBoosters.put(faction.name.toString(), boosters);
        }

        return factionBoosters;
    }

    public Set<BoostersConfig.Booster> newBoosters(Faction factions) {
        return gameApis.booster().getBoosters().keySet().stream()
            .map(boosterKey ->
                BoostersConfig.Booster.builder()
                    .key(boosterKey)
                    .range(ConfigDefaults.boosterPercent())
                    .build()
            ).collect(Collectors.toSet());
    }

    public BoostersConfig newBoostersConfig(Collection<Faction> factions) {
        // Boosters
        Map<String, Set<BoostersConfig.Booster>> boosters = newBoosters(factions);
        return BoostersConfig.builder()
            .faction(boosters)
            .build();
    }

    public BoostersConfig newBoostersConfig() {
        // Boosters
        Map<String, Set<BoostersConfig.Booster>> boosters = newBoosters(gameApis.faction().getFactionNPCs().values());
        return BoostersConfig.builder()
            .faction(boosters)
            .player(newBoosters(gameApis.faction().getPlayer()))
            .build();
    }

    public Map<String, Range> newWeather() {
        return gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.weather()));
    }

    public Sounds newSounds() {
        // Sounds Ambience
        Map<String, Range> ambienceSounds = gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Room
        Map<String, Range> roomSounds = gameApis.sounds().getRoomSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        // Sounds Settlement
        Map<String, Range> settlementSounds = gameApis.sounds().getSettlementSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        return Sounds.builder()
            .ambience(ambienceSounds)
            .settlement(settlementSounds)
            .room(roomSounds)
            .build();
    }

    public Events newEvents() {
        // Events Chance
        Map<String, Range> eventChances = gameApis.events().getEventsChance().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> ConfigDefaults.eventChance()));

        // Events Settlement
        Map<String, Boolean> settlementEvents = gameApis.events().getSettlementEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        // Event World
        Map<String, Boolean> worldEvents = gameApis.events().getWorldEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        return Events.builder()
            .world(worldEvents)
            .settlement(settlementEvents)
            .chance(eventChances)
            .build();
    }

    public Metrics newMetrics() {
        // Metrics
        Metrics metrics = ConfigDefaults.metrics();
        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        metrics.setStats(availableStats);

        return metrics;
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

    public static Range boosterPercent() {
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
