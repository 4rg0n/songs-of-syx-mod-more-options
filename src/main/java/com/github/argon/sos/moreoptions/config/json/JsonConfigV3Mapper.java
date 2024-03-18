package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.WeatherConfig;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;

import java.util.stream.Collectors;

public class JsonConfigV3Mapper {

    public static JsonMoreOptionsV3Config map(JsonMoreOptionsV2Config config) {
        return JsonMoreOptionsV3Config.builder()
            .logLevel(config.getLogLevel())
            .events(config.getEvents())
            .sounds(config.getSounds())
            .metrics(config.getMetrics())
            .boostersPlayer(config.getBoosters().entrySet().stream()
                .map(boosterEntry -> BoostersConfig.Booster.builder()
                    .key(boosterEntry.getKey())
                    .range(boosterEntry.getValue())
                    .build())
                .collect(Collectors.toSet()))
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
