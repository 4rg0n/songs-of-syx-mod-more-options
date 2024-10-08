package com.github.argon.sos.moreoptions.config.json.v5;

import com.github.argon.sos.moreoptions.config.domain.EventsConfig;
import com.github.argon.sos.moreoptions.config.domain.MetricsConfig;
import com.github.argon.sos.moreoptions.config.domain.WeatherConfig;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMetricsV2Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonBoostersV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonEventsV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonMoreOptionsV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonRacesV4Config;

import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonConfigV5Mapper {

    public static JsonMoreOptionsV5Config map(JsonMoreOptionsV4Config config) {
        return JsonMoreOptionsV5Config.builder()
            .logLevel(config.getLogLevel())
            .events(map(config.getEvents()))
            .sounds(config.getSounds())
            .metrics(map(config.getMetrics()))
            .boostersPlayer(config.getBoostersPlayer())
            .weather(WeatherConfig.builder()
                .effects(config.getWeather().getEffects())
                .build())
            .build();
    }

    public static EventsConfig map(JsonEventsV4Config eventsV4Config) {
        return EventsConfig.builder()
            .events(eventsV4Config.getEvents())
            .enemyBattleLoot(eventsV4Config.getEnemyBattleLoot())
            .playerBattleLoot(eventsV4Config.getPlayerBattleLoot())
            .build();
    }

    public static JsonRacesV5Config map(JsonRacesV4Config config) {
        return JsonRacesV5Config.builder()
            .likings(config.getLikings())
            .build();
    }

    public static JsonBoostersV5Config map(JsonBoostersV4Config config) {
        return JsonBoostersV5Config.builder()
            .faction(config.getFaction())
            .build();
    }

    public static MetricsConfig map(JsonMetricsV2Config config) {
        return MetricsConfig.builder()
            .enabled(config.isEnabled())
            .exportRateMinutes(config.getExportRateMinutes())
            .collectionRateSeconds(config.getCollectionRateSeconds())
            .stats(config.getStats().stream().collect(Collectors.toMap(
                Function.identity(),
                stat -> true
            )))
            .build();
    }
}
