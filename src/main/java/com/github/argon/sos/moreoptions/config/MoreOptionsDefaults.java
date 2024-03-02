package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides {@link MoreOptionsV2Config} with default settings
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
    private final Map<String, MoreOptionsV2Config.Range> boostersMulti = boostersMulti();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> boostersAdd = boostersAdd();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> weather = weather();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> soundsRoom = soundsRoom();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> soundsAmbience = soundsAmbience();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> soundsSettlement = soundsSettlement();
    @Getter(lazy = true)
    private final Map<String, MoreOptionsV2Config.Range> eventsChance = eventsChance();
    @Getter(lazy = true)
    private final Map<String, Boolean> eventsWorld = eventsWorld();
    @Getter(lazy = true)
    private final Map<String, Boolean> eventsSettlement = eventsSettlement();
    @Getter(lazy = true)
    private final MoreOptionsV2Config.Metrics metrics = metrics();
    @Getter(lazy = true)
    private final MoreOptionsV2Config defaults = newDefaults();


    /**
     * This thing is a little flaky!
     * Because it relies on reading game data to build the configs, it is only usable after a certain phase.
     * When called too early, some game classes might not be available yet
     * and the method could fail or deliver en empty result.
     */
    private MoreOptionsV2Config newDefaults() {
        return MoreOptionsV2Config.builder()
            .filePath(ConfigStore.configPath())
            .events(MoreOptionsV2Config.Events.builder()
                .world(getEventsWorld())
                .settlement(getEventsSettlement())
                .chance(getEventsChance())
                .build())
            .sounds(MoreOptionsV2Config.Sounds.builder()
                .ambience(getSoundsAmbience())
                .settlement(getSoundsSettlement())
                .room(getSoundsRoom())
                .build())
            .weather(getWeather())
            .boosters(getBoostersMulti())
            .metrics(getMetrics())
            .build();
    }

    private MoreOptionsV2Config.Metrics metrics() {
        return MoreOptionsV2Config.Metrics.builder()
            .build();
    }

    private Map<String, MoreOptionsV2Config.Range> boostersMulti() {
        //noinspection DataFlowIssue
        return gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(1)
                .max(10000)
                .applyMode(MoreOptionsV2Config.Range.ApplyMode.MULTI)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> boostersAdd() {
        //noinspection DataFlowIssue
        return gameApis.booster().getBoosters().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(0)
                .min(0)
                .max(10000)
                .applyMode(MoreOptionsV2Config.Range.ApplyMode.ADD)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.ABSOLUTE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> weather() {
        //noinspection DataFlowIssue
        return gameApis.weather().getWeatherThings().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> soundsRoom() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getRoomSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> soundsSettlement() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getSettlementSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> soundsAmbience() {
        //noinspection DataFlowIssue
        return gameApis.sounds().getAmbienceSounds().keySet().stream()
            .collect(Collectors.toMap(key -> key, o -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(100)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
                .build()));
    }

    private Map<String, MoreOptionsV2Config.Range> eventsChance() {
        //noinspection DataFlowIssue
        return gameApis.events().getEventsChance().keySet().stream()
            .collect(Collectors.toMap(key -> key, key -> MoreOptionsV2Config.Range.builder()
                .value(100)
                .min(0)
                .max(10000)
                .displayMode(MoreOptionsV2Config.Range.DisplayMode.PERCENTAGE)
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
