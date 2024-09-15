package com.github.argon.sos.moreoptions.config.json.v4;

import com.github.argon.sos.moreoptions.config.domain.SoundsConfig;
import com.github.argon.sos.moreoptions.config.domain.WeatherConfig;
import com.github.argon.sos.moreoptions.config.json.v3.*;

public class JsonConfigV4Mapper {

    public static JsonMoreOptionsV4Config map(JsonMoreOptionsV3Config config) {
        return JsonMoreOptionsV4Config.builder()
            .logLevel(config.getLogLevel())
            .events(map(config.getEvents()))
            .sounds(map(config.getSounds()))
            .metrics(config.getMetrics())
            .boostersPlayer(config.getBoostersPlayer())
            .weather(WeatherConfig.builder()
                .effects(config.getWeather().getEffects())
                .build())
            .build();
    }

    public static JsonEventsV4Config map(JsonEventsV3Config eventsV3Config) {
        return JsonEventsV4Config.builder()
            .chance(eventsV3Config.getChance())
            .enemyBattleLoot(eventsV3Config.getEnemyBattleLoot())
            .playerBattleLoot(eventsV3Config.getPlayerBattleLoot())
            .build();
    }

    public static SoundsConfig map(JsonSoundsV3Config soundsV3Config) {
        return SoundsConfig.builder().build();
    }

    public static JsonRacesV4Config map(JsonRacesV3Config config) {
        return JsonRacesV4Config.builder()
            .likings(config.getLikings())
            .build();
    }

    public static JsonBoostersV4Config map(JsonBoostersV3Config config) {
        return JsonBoostersV4Config.builder()
            .faction(config.getFaction())
            .build();
    }
}
