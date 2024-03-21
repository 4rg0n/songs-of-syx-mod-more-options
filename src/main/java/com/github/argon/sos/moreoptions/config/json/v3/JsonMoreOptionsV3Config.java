package com.github.argon.sos.moreoptions.config.json.v3;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config.VERSION;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMoreOptionsV3Config {
    @Builder.Default
    private int version = VERSION;
    @Builder.Default
    private String logLevel = ConfigDefaults.LOG_LEVEL.getName();

    @Builder.Default
    private SoundsConfig sounds = SoundsConfig.builder().build();
    @Builder.Default
    private EventsConfig events = EventsConfig.builder().build();
    @Builder.Default
    private WeatherConfig weather = WeatherConfig.builder().build();
    @Builder.Default
    private MetricsConfig metrics = MetricsConfig.builder().build();
    @Builder.Default
    private Map<String, BoostersConfig.Booster> boostersPlayer = new HashMap<>();
    @Builder.Default
    private Map<String, BoostersConfig.BoostersPreset> boostersPresets = new HashMap<>();
}
