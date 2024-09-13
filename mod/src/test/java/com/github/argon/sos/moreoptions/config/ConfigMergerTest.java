package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.mod.sdk.util.Maps;
import com.github.argon.sos.mod.sdk.util.Sets;
import com.github.argon.sos.moreoptions.config.domain.MetricsConfig;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.testing.ModExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModExtension.class)
class ConfigMergerTest {

    @Test
    void mergeConfig() {
        // Nothing should happen
        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newConfig())).isEqualTo(TestData.newConfig());
        assertThat(ConfigMerger.merge(TestData.newEmptyConfig(), TestData.newEmptyConfig())).isEqualTo(TestData.newEmptyConfig());

        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newEmptyConfig())).isEqualTo(TestData.newMergedEmptyConfig());
        assertThat(ConfigMerger.merge(TestData.newConfig(), TestData.newDefaultConfig())).isEqualTo(TestData.newMergedDefaultConfig());
    }

    @Test
    void mergeRacesConfig() {
        RacesConfig racesConfig = RacesConfig.builder()
            .likings(Sets.Modifiable.of(RacesConfig.Liking.builder()
                .race("CONFIG_RACE")
                .otherRace("OTHER_CONFIG_RACE")
                .range(Range.builder()
                    .value(111)
                    .build())
                .build(),
                RacesConfig.Liking.builder()
                    .race("NOT_EXISTING")
                    .otherRace("OTHER_NOT_EXISTING")
                    .range(Range.builder()
                        .value(111)
                        .build())
                    .build()))
            .build();

        RacesConfig expectedConfig = RacesConfig.builder()
            .likings(Sets.Modifiable.of(RacesConfig.Liking.builder()
                    .race("CONFIG_RACE")
                    .otherRace("OTHER_CONFIG_RACE")
                    .range(Range.builder()
                        .value(111)
                        .build())
                    .build(),
                RacesConfig.Liking.builder()
                    .race("DEFAULT_RACE")
                    .otherRace("OTHER_DEFAULT_RACE")
                    .range(Range.builder()
                        .value(999)
                        .build())
                    .build()
            ))
            .build();

        ConfigMerger.merge(
            racesConfig,
            RacesConfig.builder()
                .likings(Sets.Modifiable.of(
                    RacesConfig.Liking.builder()
                        .race("CONFIG_RACE")
                        .otherRace("OTHER_CONFIG_RACE")
                        .range(Range.builder()
                            .value(222)
                            .build())
                        .build(),
                    RacesConfig.Liking.builder()
                    .race("DEFAULT_RACE")
                    .otherRace("OTHER_DEFAULT_RACE")
                    .range(Range.builder()
                        .value(999)
                        .build())
                    .build()))
                .build());

        assertThat(racesConfig).isEqualTo(expectedConfig);
    }

    @Test
    void mergeMetricsConfig() {
        MetricsConfig metricsConfig = MetricsConfig.builder()
            .collectionRateSeconds(Range.builder()
                .value(111)
                .build())
            .exportRateMinutes(Range.builder()
                .value(222)
                .build())
            .enabled(true)
            .stats(Maps.Modifiable.of(
                "stat1", true,
                "stat2", false
            ))
            .build();

        ConfigMerger.merge(metricsConfig, MetricsConfig.builder()
            .collectionRateSeconds(Range.builder().build())
            .exportRateMinutes(Range.builder().build())
            .enabled(false)
            .stats(Maps.Modifiable.of(
                "stat1", true,
                "stat2", true,
                "stat3", true
            ))
            .build());

        MetricsConfig expectedMetricsConfig = MetricsConfig.builder()
            .collectionRateSeconds(Range.builder()
                .value(111)
                .build())
            .exportRateMinutes(Range.builder()
                .value(222)
                .build())
            .enabled(true)
            .stats(Maps.Modifiable.of(
                "stat1", true,
                "stat2", false,
                "stat3", true
            ))
            .build();

        assertThat(metricsConfig).isEqualTo(expectedMetricsConfig);
    }
}