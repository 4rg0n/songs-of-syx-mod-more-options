package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.mod.sdk.util.Sets;
import com.github.argon.sos.moreoptions.config.domain.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestData {
    public static MoreOptionsV5Config newDefaultConfig() {
        return MoreOptionsV5Config.builder()
            .events(EventsConfig.builder()
                .events(Maps.of(
                    "default1", true,
                    "default2", false
                ))
                .chance(Maps.of(
                    "default1", Range.builder().build()
                ))
                .playerBattleLoot(Range.builder().build())
                .enemyBattleLoot(Range.builder().build())
                .build())
            .sounds(SoundsConfig.builder()
                .ambience(Maps.of(
                    "default1", Range.builder().build()
                ))
                .build())
            .weather(WeatherConfig.builder()
                .effects(Maps.of(
                    "default1", Range.builder().build()
                ))
                .build())
            .boosters(BoostersConfig.builder()
                .presets(Maps.of("default1", BoostersConfig.BoostersPreset.builder()
                    .name("default1")
                    .boosters(Maps.of(
                        "default1", BoostersConfig.Booster.builder()
                            .key("default1")
                            .range(Range.builder().build())
                            .build()
                    ))
                    .build()))
                .player(Maps.of(
                    "default1", BoostersConfig.Booster.builder()
                        .key("default1")
                        .range(Range.builder().build())
                        .build()
                ))
                .faction(Maps.of(
                    "default1", Maps.of(
                        "default1", BoostersConfig.Booster.builder()
                            .key("default1")
                            .range(Range.builder().build())
                            .build()
                    )
                ))
                .build())
            .metrics(MetricsConfig.builder()
                .collectionRateSeconds(Range.builder().build())
                .exportRateMinutes(Range.builder().build())
                .enabled(true)
                .stats(Sets.of("default1", "default2", "default3"))
                .build())
            .races(RacesConfig.builder()
                .likings(Sets.of(
                    RacesConfig.Liking.builder()
                        .race("default")
                        .otherRace("default")
                        .range(Range.builder().build())
                        .build()
                ))
                .build())
            .build();
    }

    public static MoreOptionsV5Config newConfig() {
        return MoreOptionsV5Config.builder()
            .events(EventsConfig.builder()
                .events(Maps.of(
                    "test1", true,
                    "test2", false
                ))
                .chance(Maps.of(
                    "test1", Range.builder().build()
                ))
                .playerBattleLoot(Range.builder().build())
                .enemyBattleLoot(Range.builder().build())
                .build())
            .sounds(SoundsConfig.builder()
                .ambience(Maps.of(
                    "test1", Range.builder().build()
                ))
                .build())
            .weather(WeatherConfig.builder()
                .effects(Maps.of(
                    "test1", Range.builder().build()
                ))
                .build())
            .boosters(BoostersConfig.builder()
                .presets(Maps.of("test1", BoostersConfig.BoostersPreset.builder()
                    .name("test1")
                    .boosters(Maps.of(
                        "test1", BoostersConfig.Booster.builder()
                            .key("test1")
                            .range(Range.builder().build())
                            .build()
                    ))
                    .build()))
                .player(Maps.of(
                    "test1", BoostersConfig.Booster.builder()
                        .key("test1")
                        .range(Range.builder().build())
                        .build()
                ))
                .faction(Maps.of(
                    "test1", Maps.of(
                        "test1", BoostersConfig.Booster.builder()
                            .key("test1")
                            .range(Range.builder().build())
                            .build()
                    )
                ))
                .build())
            .metrics(MetricsConfig.builder()
                .collectionRateSeconds(Range.builder().build())
                .exportRateMinutes(Range.builder().build())
                .enabled(true)
                .stats(Sets.of("test1", "test2", "test3"))
                .build())
            .races(RacesConfig.builder()
                .likings(Sets.of(
                    RacesConfig.Liking.builder()
                        .race("test")
                        .otherRace("test")
                        .range(Range.builder().build())
                        .build()
                ))
                .build())
            .build();
    }
}
