package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.log.Level;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsV5Config {

    public final static int VERSION = 5;

    @Builder.Default
    private int version = VERSION;
    @Builder.Default
    private Level logLevel = ConfigDefaults.LOG_LEVEL;

    @Builder.Default
    private SoundsConfig sounds = SoundsConfig.builder().build();
    @Builder.Default
    private EventsConfig events = EventsConfig.builder().build();
    @Builder.Default
    private WeatherConfig weather = WeatherConfig.builder().build();
    @Builder.Default
    private BoostersConfig boosters = BoostersConfig.builder().build();
    @Builder.Default
    private MetricsConfig metrics = MetricsConfig.builder().build();
    @Builder.Default
    private RacesConfig races = RacesConfig.builder().build();

    public String toJson() {
        Json json = new Json(JsonMapper.mapObject(this), JsonWriters.jsonEPretty());
        return json.write();
    }
}
