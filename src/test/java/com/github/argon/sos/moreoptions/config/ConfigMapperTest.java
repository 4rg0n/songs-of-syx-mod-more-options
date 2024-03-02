package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigMapperTest {

    private ConfigMapper configMapper = Mappers.getMapper(ConfigMapper.class);

    @Test
    void update() {
        MoreOptionsV2Config configTarget = MoreOptionsV2Config.builder()
            .weather(null)
            .build();
        MoreOptionsV2Config configSource = MoreOptionsV2Config.builder()
            .logLevel(Level.INFO)
            .boosters(Maps.of(
                "booster1", ConfigDefaults.boosterMulti(),
                "booster2", ConfigDefaults.boosterMulti(),
                "booster3", ConfigDefaults.boosterMulti()
            ))
            .weather(
                Maps.of(
                    "weather1", ConfigDefaults.weather(),
                    "weather2", ConfigDefaults.weather(),
                    "weather3", ConfigDefaults.weather()
                )
            )
            .metrics(MoreOptionsV2Config.Metrics.builder()
                .stats(Lists.of("stat1", "stat2", "stat3"))
                .build())
            .build();

        configMapper.update(configTarget, configSource);

        assertThat(configTarget).isEqualTo(configSource);
    }
}