package com.github.argon.sos.moreoptions.config.json.v2;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.MetricsConfig;
import com.github.argon.sos.moreoptions.config.domain.Range;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config.VERSION;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMoreOptionsV2Config {
    @Builder.Default
    private int version = VERSION;
    @Builder.Default
    private String logLevel = ConfigDefaults.LOG_LEVEL.getName();
    @Builder.Default
    private Map<String, Boolean> eventsSettlement = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> eventsWorld = new HashMap<>();
    @Builder.Default
    private Map<String, Range> eventsChance = new HashMap<>();
    @Builder.Default
    private Map<String, Range> soundsAmbience = new HashMap<>();
    @Builder.Default
    private Map<String, Range> soundsSettlement = new HashMap<>();
    @Builder.Default
    private Map<String, Range> soundsRoom = new HashMap<>();
    @Builder.Default
    private Map<String, Range> weather = new HashMap<>();
    @Builder.Default
    private Map<String, Range> boosters = new HashMap<>();
    @Builder.Default
    private MetricsConfig metrics = MetricsConfig.builder().build();
}
