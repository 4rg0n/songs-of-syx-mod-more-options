package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.JsonMeta;
import com.github.argon.sos.moreoptions.config.json.v5.JsonBoostersV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonMoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonRacesV5Config;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMapper {
    public static MoreOptionsV5Config mapConfig(JsonMoreOptionsV5Config config) {
       return MoreOptionsV5Config.builder()
            .logLevel(Level.fromName(config.getLogLevel())
                .orElse(ConfigDefaults.LOG_LEVEL))
            .version(config.getVersion())
            .sounds(config.getSounds())
            .weather(config.getWeather())
            .metrics(config.getMetrics())
            .events(config.getEvents())
            .boosters(BoostersConfig.builder()
                .presets(config.getBoostersPresets())
                .player(config.getBoostersPlayer())
                .build())
            .build();
    }

    public static JsonMoreOptionsV5Config mapConfig(MoreOptionsV5Config config) {
        return JsonMoreOptionsV5Config.builder()
            .logLevel(config.getLogLevel().getName())
            .version(config.getVersion())
            .sounds(config.getSounds())
            .weather(config.getWeather())
            .metrics(config.getMetrics())
            .events(config.getEvents())
            .boostersPlayer(config.getBoosters().getPlayer())
            .boostersPresets(config.getBoosters().getPresets())
            .build();
    }

    public static JsonRacesV5Config mapRacesConfig(MoreOptionsV5Config config) {
        return JsonRacesV5Config.builder()
            .likings(config.getRaces().getLikings())
            .build();
    }

    public static JsonBoostersV5Config mapBoostersConfig(MoreOptionsV5Config config) {
        return JsonBoostersV5Config.builder()
            .faction(config.getBoosters().getFaction())
            .build();
    }

    public static ConfigMeta mapMeta(JsonMeta config) {
        return ConfigMeta.builder()
            .logLevel(Level.fromName(config.getLogLevel())
                .orElse(ConfigDefaults.LOG_LEVEL))
            .version(config.getVersion())
            .build();
    }

    public static void mapInto(JsonRacesV5Config racesConfig, MoreOptionsV5Config config) {
        config.getRaces().setLikings(racesConfig.getLikings());
    }

    public static void mapInto(JsonBoostersV5Config boostersConfig, MoreOptionsV5Config config) {
        config.getBoosters().setFaction(boostersConfig.getFaction());
    }
}
