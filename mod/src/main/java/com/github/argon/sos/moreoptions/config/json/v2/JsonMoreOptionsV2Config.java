package com.github.argon.sos.moreoptions.config.json.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config.VERSION;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private JsonMetricsV2Config metrics = JsonMetricsV2Config.builder().build();
}
