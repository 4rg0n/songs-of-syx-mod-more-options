package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides {@link MoreOptionsConfig} with default settings
 * Accessing defaults before the "game instance created" phase can cause problems:
 * Some config is generated from data provided by the {@link GameApis}.
 * These access classes of the game, which might not have been initialized yet.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsDefaults {
    @Getter(lazy = true)
    private final static MoreOptionsDefaults instance = new MoreOptionsDefaults(
        GameApis.getInstance()
    );

    private final GameApis gameApis;

    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> boostersMulti = boostersMulti();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> boostersAdd = boostersAdd();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> weather = weather();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> soundsRoom = soundsRoom();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> soundsAmbience = soundsAmbience();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> soundsSettlement = soundsSettlement();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsConfig.Range> eventsChance = eventsChance();
    @Getter(lazy = true)
    private final Map<String, Boolean> eventsWorld = eventsWorld();
    @Getter(lazy = true)
    private final Map<String, Boolean> eventsSettlement = eventsSettlement();
    @Getter(lazy = true)
    private final MoreOptionsConfig.Metrics metrics = metrics();
    @Getter(lazy = true)
    private final MoreOptionsConfig defaults = newDefaults();


    /**
     * This thing is a little flaky!
     * Because it relies on reading game data to build the configs, it is only usable after a certain phase.
     * When called too early, some game classes might not be available yet
     * and the method could fail or deliver en empty result.
     */
    private MoreOptionsConfig newDefaults() {
        return MoreOptionsConfig.builder()
            .filePath(ConfigStore.configPath())
            .events(MoreOptionsConfig.Events.builder()
                .world(getEventsWorld())
                .settlement(getEventsSettlement())
                .chance(getEventsChance())
                .build())
            .sounds(MoreOptionsConfig.Sounds.builder()
                .ambience(getSoundsAmbience())
                .settlement(getSoundsSettlement())
                .room(getSoundsRoom())
                .build())
            .weather(getWeather())
            .boosters(getBoostersMulti())
            .metrics(getMetrics())
            .build();
    }

    private MoreOptionsConfig.Metrics metrics() {
        return MoreOptionsConfig.Metrics.builder()
            .build();
    }

    private Map<String, MoreOptionsConfig.Range> boostersMulti() {
        //noinspection DataFlowIssue
        return gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(1)
                .max(10000)
                .applyMode(MoreOptionsConfig.Range.ApplyMode.MULTI)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> boostersAdd() {
        //noinspection DataFlowIssue
        return gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(0)
                .min(0)
                .max(10000)
                .applyMode(MoreOptionsConfig.Range.ApplyMode.ADD)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.ABSOLUTE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> weather() {
        //noinspection DataFlowIssue
        return gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> soundsRoom() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getRoomSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> soundsSettlement() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getSettlementSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> soundsAmbience() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsConfig.Range> eventsChance() {
        //noinspection DataFlowIssue
        return gameApis.events().getEventsChance().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> MoreOptionsConfig.Range.builder()
                .value(100)
                .min(0)
                .max(10000)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, Boolean> eventsSettlement() {
        //noinspection DataFlowIssue
        return gameApis.events().getSettlementEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));
    }

    private Map<String, Boolean> eventsWorld() {
        //noinspection DataFlowIssue
        return gameApis.events().getWorldEvents().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> true));
    }
}
