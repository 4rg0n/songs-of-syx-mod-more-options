package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.domain.*;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.faction.Faction;
import init.paths.PATHS;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config.*;

/**
 * Provides default configuration partially gathered from the game.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigDefaults {

    private final static Logger log = Loggers.getLogger(ConfigDefaults.class);

    public final static Level LOG_LEVEL = Level.INFO;

    public final static Path PROFILE_PATH = PATHS.local().PROFILE.get().resolve(MoreOptionsScript.MOD_INFO.name.toString());
    public final static Path CONFIGE_PATH = PATHS.local().SETTINGS.get();
    public final static Path CONFIG_FILE_PATH = CONFIGE_PATH.resolve("MoreOptions.txt");

    public final static Path RACES_CONFIG_FOLDER_PATH = PROFILE_PATH.resolve("races");
    public final static Path BOOSTERS_CONFIG_FOLDER_PATH = PROFILE_PATH.resolve("boosters");

    @Getter(lazy = true)
    private final static ConfigDefaults instance = new ConfigDefaults(
        GameApis.getInstance()
    );

    private final GameApis gameApis;

    public MoreOptionsV5Config newConfig() {
        log.debug("Creating new default config");

        MoreOptionsV5Config defaultConfig = builder()
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

    public Map<String, Map<String, BoostersConfig.Booster>> newBoosters(Collection<? extends Faction> factions) {
        Map<String, Map<String, BoostersConfig.Booster>> factionBoosters = new HashMap<>();
        for (Faction faction : factions) {
            Map<String, BoostersConfig.Booster> boosters = newBoosters();
            factionBoosters.put(faction.name.toString(), boosters);
        }

        return factionBoosters;
    }

    public Map<String, BoostersConfig.Booster> newBoosters() {
        return gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(
                boosterKey -> boosterKey,
                boosterKey -> BoostersConfig.Booster.builder()
                    .key(boosterKey)
                    .range(ConfigDefaults.boosterPercent())
                    .build()
            ));
    }

    public BoostersConfig newBoostersConfig(Collection<Faction> factions) {
        // Boosters
        Map<String, Map<String, BoostersConfig.Booster>> boosters = newBoosters(factions);
        return BoostersConfig.builder()
            .faction(boosters)
            .build();
    }

    public BoostersConfig newBoostersConfig() {
        // Boosters
        Map<String, Map<String, BoostersConfig.Booster>> boosters = newBoosters(gameApis.faction().getFactionNPCs().values());
        return BoostersConfig.builder()
            .faction(boosters)
            .player(newBoosters())
            .build();
    }

    public WeatherConfig newWeather() {
        Map<String, Range> effects = gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.weather()));

        return WeatherConfig.builder()
            .effects(effects)
            .build();
    }

    public SoundsConfig newSounds() {
        // Sounds Ambience
        Map<String, Range> ambienceSounds = gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> ConfigDefaults.sound()));

        return SoundsConfig.builder()
            .ambience(ambienceSounds)
            .build();
    }

    public EventsConfig newEvents() {
        // Events Chance
        Map<String, Range> eventChances = gameApis.events().getEventsChances().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> ConfigDefaults.eventChance()));

        // Events
        Map<String, Boolean> events = gameApis.events().getEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));

        return EventsConfig.builder()
            .events(events)
            .chance(eventChances)
            .enemyBattleLoot(battleLootEnemy())
            .playerBattleLoot(battleLootPlayer())
            .build();
    }

    public MetricsConfig newMetrics() {
        // Metrics
        MetricsConfig metricsConfig = ConfigDefaults.metrics();
        Set<String> availableStats = gameApis.stats().getAvailableStatKeys();
        metricsConfig.setStats(availableStats);

        return metricsConfig;
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

    public static MetricsConfig metrics() {
        return MetricsConfig.builder().build();
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
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }

    public static Range sound() {
        return Range.builder()
            .value(100)
            .min(0)
            .max(100)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }

    public static Range eventChance() {
        return Range.builder()
            .value(100)
            .min(0)
            .max(10000)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }

    public static Range battleLootPlayer() {
        return Range.builder()
            .value(25)
            .min(0)
            .max(10000)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }

    public static Range battleLootEnemy() {
        return Range.builder()
            .value(100)
            .min(0)
            .max(10000)
            .displayMode(Range.DisplayMode.PERCENTAGE)
            .applyMode(Range.ApplyMode.PERCENT)
            .build();
    }
}
