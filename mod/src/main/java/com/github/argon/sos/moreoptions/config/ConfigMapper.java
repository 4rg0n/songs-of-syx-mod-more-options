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
    /**
     * Maps a JSON config into its domain representation.
     *
     * @param config JSON config to map
     * @return mapped domain config
     */
    public static MoreOptionsV5Config mapConfig(JsonMoreOptionsV5Config config) {
       return MoreOptionsV5Config.builder()
           .logLevel(Level.fromName(config.getLogLevel())
               .orElse(ConfigDefaults.LOG_LEVEL))
           .logToFile(config.isLogToFile())
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

    /**
     * Maps a domain config into its JSON representation.
     *
     * @param config domain config to map
     * @return mapped JSON config
     */
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

    /**
     * Extracts and maps the races part of a domain config into its JSON representation.
     *
     * @param config domain config to map
     * @return mapped JSON races config
     */
    public static JsonRacesV5Config mapRacesConfig(MoreOptionsV5Config config) {
        return JsonRacesV5Config.builder()
            .likings(config.getRaces().getLikings())
            .build();
    }

    /**
     * Extracts and maps the boosters part of a domain config into its JSON representation.
     *
     * @param config domain config to map
     * @return mapped JSON boosters config
     */
    public static JsonBoostersV5Config mapBoostersConfig(MoreOptionsV5Config config) {
        return JsonBoostersV5Config.builder()
            .faction(config.getBoosters().getFaction())
            .build();
    }

    /**
     * Maps JSON meta data into its domain representation.
     *
     * @param config JSON meta data to map
     * @return mapped domain meta data
     */
    public static ConfigMeta mapMeta(JsonMeta config) {
        return ConfigMeta.builder()
            .logLevel(Level.fromName(config.getLogLevel())
                .orElse(ConfigDefaults.LOG_LEVEL))
            .logToFile(config.isLogToFile())
            .version(config.getVersion())
            .build();
    }

    /**
     * Copies the races config from JSON into an existing domain config, mutating it in place.
     *
     * @param racesConfig JSON races config to copy from
     * @param config domain config to mutate
     */
    public static void mapInto(JsonRacesV5Config racesConfig, MoreOptionsV5Config config) {
        config.getRaces().setLikings(racesConfig.getLikings());
    }

    /**
     * Copies the boosters config from JSON into an existing domain config, mutating it in place.
     *
     * @param boostersConfig JSON boosters config to copy from
     * @param config domain config to mutate
     */
    public static void mapInto(JsonBoostersV5Config boostersConfig, MoreOptionsV5Config config) {
        config.getBoosters().setFaction(boostersConfig.getFaction());
    }
}
