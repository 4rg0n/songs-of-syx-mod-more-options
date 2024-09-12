package com.github.argon.sos.moreoptions.config.json.v3;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.WeatherConfig;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonConfigV3Mapper {

    public static JsonMoreOptionsV3Config map(JsonMoreOptionsV2Config config) {
        return JsonMoreOptionsV3Config.builder()
            .logLevel(config.getLogLevel())
            .events(JsonEventsV3Config.builder()
                .settlement(config.getEventsSettlement())
                .world(config.getEventsWorld())
                .chance(config.getEventsChance())
                .build())
            .sounds(JsonSoundsV3Config.builder()
                .settlement(config.getSoundsSettlement())
                .room(config.getSoundsRoom())
                .ambience(config.getSoundsAmbience())
                .build())
            .metrics(config.getMetrics())
            .boostersPlayer(config.getBoosters().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    boosterEntry -> BoostersConfig.Booster.builder()
                        .key(boosterEntry.getKey())
                        .range(boosterEntry.getValue())
                        .build()
                )))
            .weather(WeatherConfig.builder()
                .effects(config.getWeather())
                .build())
            .build();
    }

    public static JsonRacesV3Config map(JsonRacesV2Config config) {
        return JsonRacesV3Config.builder()
            .likings(config.getLikings())
            .build();
    }
}
