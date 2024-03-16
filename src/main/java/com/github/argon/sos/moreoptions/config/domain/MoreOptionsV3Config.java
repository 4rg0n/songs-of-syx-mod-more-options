package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsV3Config {

    public final static int VERSION = 3;

    @Builder.Default
    private int version = VERSION;
    @Builder.Default
    private Level logLevel = ConfigDefaults.CONFIG_DEFAULT_LOG_LEVEL;

    @Builder.Default
    private SoundsConfig sounds = SoundsConfig.builder().build();
    @Builder.Default
    private EventsConfig events = EventsConfig.builder().build();
    @Builder.Default
    private Map<String, Range> weather = new HashMap<>();
    @Builder.Default
    private BoostersConfig boosters = BoostersConfig.builder().build();
    @Builder.Default
    private MetricsConfig metrics = MetricsConfig.builder().build();
    @Builder.Default
    private RacesConfig races = RacesConfig.builder().build();

    /* TODO
       * day night / cycle; lightning
       * toggle deposit overlay when building

     */

    /**
     * Only for debugging purposes
     */
    public String toJson() {
        Json json = new Json(JsonMapper.mapObject(this));
        return json.toString();
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Meta {
        @Builder.Default
        private int version = VERSION;
        @Builder.Default
        private Level logLevel = Loggers.LOG_LEVEL_DEFAULT;
    }
}
