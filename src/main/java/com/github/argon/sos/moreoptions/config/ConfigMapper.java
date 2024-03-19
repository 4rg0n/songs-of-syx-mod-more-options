package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.JsonMeta;
import com.github.argon.sos.moreoptions.config.json.v3.JsonBoostersV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.log.Level;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMapper {
    public static MoreOptionsV3Config mapConfig(JsonMoreOptionsV3Config config) {
       return MoreOptionsV3Config.builder()
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

    public static JsonMoreOptionsV3Config mapConfig(MoreOptionsV3Config config) {
        return JsonMoreOptionsV3Config.builder()
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

    public static JsonRacesV3Config mapRacesConfig(MoreOptionsV3Config config) {
        return JsonRacesV3Config.builder()
            .likings(config.getRaces().getLikings())
            .build();
    }

    public static JsonBoostersV3Config mapBoostersConfig(MoreOptionsV3Config config) {
        return JsonBoostersV3Config.builder()
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

    public static void mapInto(JsonRacesV3Config racesConfig, MoreOptionsV3Config config) {
        config.getRaces().setLikings(racesConfig.getLikings());
    }

    public static void mapInto(JsonBoostersV3Config boostersConfig, MoreOptionsV3Config config) {
        config.getBoosters().setFaction(boostersConfig.getFaction());
    }
}
