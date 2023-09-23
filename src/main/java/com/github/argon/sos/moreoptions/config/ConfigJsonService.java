package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.Optional;

/**
 * For saving and loading {@link MoreOptionsConfig} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigJsonService {
    private final static Logger log = Loggers.getLogger(ConfigJsonService.class);

    @Getter(lazy = true)
    private final static ConfigJsonService instance = new ConfigJsonService(PATHS.local().SETTINGS);

    private final PATH savePath;

    /**
     * Configuration from games user profile
     */
    public Optional<MoreOptionsConfig> loadConfig() {
        return load(savePath);
    }

    public boolean saveConfig(MoreOptionsConfig config) {
        log.debug("Saving configuration into %s", savePath.get().toString());
        log.trace("CONFIG: %s", config);

        try {
            JsonE configJson = new JsonE();
            configJson.add("EVENTS", mapEvents(config.getEvents()));
            configJson.add("SOUNDS", mapSounds(config.getAmbienceSounds()));
            configJson.add("WEATHER", mapWeather(config.getWeather()));

            // save file exists?
            Path path;
            if (!savePath.exists(MoreOptionsConfig.FILE_NAME)) {
                path = savePath.create(MoreOptionsConfig.FILE_NAME);
                log.debug("Created new configuration file %s", path);
            } else {
                path = savePath.get(MoreOptionsConfig.FILE_NAME);
            }

            boolean success = configJson.save(path);

            log.debug("Saving to %s was successful? %s", path, success);

            return success;
        } catch (Errors.DataError e) {
            log.warn("Could not save configuration into: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Could not save configuration", e);
        }

        return false;
    }

    public Optional<MoreOptionsConfig> load(PATH path) {
        log.debug("Loading json config from %s", path.get());
        if (!path.exists(MoreOptionsConfig.FILE_NAME)) {
            // do not load what's not there
            log.debug("Configuration %s/%s not present using defaults.", path.get(), MoreOptionsConfig.FILE_NAME);
            return Optional.empty();
        }

        Path loadPath = path.get(MoreOptionsConfig.FILE_NAME);
        return load(loadPath);
    }

    public Optional<MoreOptionsConfig> load(Path path) {
        Json json;

        try {
            json = new Json(path);
        }  catch (Exception e) {
            log.info("Could not load json config from %s", path.toString(), e);
            return Optional.empty();
        }

        return Optional.of(MoreOptionsConfig.builder()
            .events((json.has("EVENTS")) ? mapEvents(json.json("EVENTS")) : MoreOptionsConfig.Events.builder().build())
            .ambienceSounds((json.has("SOUNDS")) ? mapSounds(json.json("SOUNDS")) : MoreOptionsConfig.AmbienceSounds.builder().build())
            .weather((json.has("WEATHER")) ? mapWeather(json.json("WEATHER")) : MoreOptionsConfig.Weather.builder().build())
            .build());
    }

    private MoreOptionsConfig.Weather mapWeather(Json weather) {
        return MoreOptionsConfig.Weather.builder()
            .clouds(weather.i("CLOUDS", 0, 100, 100))
            .ice(weather.i("ICE", 0, 100, 100))
            .snow(weather.i("SNOW", 0, 100, 100))
            .thunder(weather.i("THUNDER", 0, 100, 100))
            .rain(weather.i("RAIN", 0, 100, 100))
            .build();
    }

    private JsonE mapWeather(MoreOptionsConfig.Weather config) {
        JsonE json = new JsonE();
        json.add("CLOUDS", config.getClouds());
        json.add("ICE", config.getIce());
        json.add("SNOW", config.getSnow());
        json.add("THUNDER", config.getThunder());
        json.add("RAIN", config.getRain());

        return json;
    }

    private MoreOptionsConfig.AmbienceSounds mapSounds(Json sounds) {
        return MoreOptionsConfig.AmbienceSounds.builder()
            .wind(sounds.i("WIND", 0, 100, 100))
            .water(sounds.i("WATER", 0, 100, 100))
            .windTrees(sounds.i("WIND_TREES", 0, 100, 100))
            .windHowl(sounds.i("WIND_HOWL", 0, 100, 100))
            .night(sounds.i("NIGHT", 0, 100, 100))
            .rain(sounds.i("RAIN", 0, 100, 100))
            .nature(sounds.i("NATURE", 0, 100, 100))
            .thunder(sounds.i("THUNDER", 0, 100, 100))
            .build();
    }

    private JsonE mapSounds(MoreOptionsConfig.AmbienceSounds config) {
        JsonE json = new JsonE();
        json.add("WIND", config.getWind());
        json.add("WATER", config.getWater());
        json.add("WIND_TREES", config.getWindTrees());
        json.add("WIND_HOWL", config.getWindHowl());
        json.add("NIGHT", config.getNight());
        json.add("RAIN", config.getRain());
        json.add("NATURE", config.getNature());
        json.add("THUNDER", config.getThunder());

        return json;
    }

    private MoreOptionsConfig.Events mapEvents(Json json) {
        return MoreOptionsConfig.Events.builder()
            .accident(json.bool("ACCIDENT", true))
            .advice(json.bool("ADVICE", true))
            .farm(json.bool("FARM", true))
            .fish(json.bool("FISH", true))
            .riot(json.bool("RIOT", true))
            .killer(json.bool("KILLER", true))
            .orchard(json.bool("ORCHARD", true))
            .pasture(json.bool("PASTURE", true))
            .disease(json.bool("DISEASE", true))
            .slaver(json.bool("SLAVER", true))
            .temperature(json.bool("TEMPERATURE", true))
            .raceWars(json.bool("RACE_WARS", true))
            .uprising(json.bool("UPRISING", true))
            .worldFactionBreak(json.bool("WORLD_FACTION_BREAK", true))
            .worldRaider(json.bool("WORLD_RAIDER", true))
            .worldFactionExpand(json.bool("WORLD_FACTION_EXPAND", true))
            .worldRebellion(json.bool("WORLD_REBELLION", true))
            .worldPlague(json.bool("WORLD_PLAGUE", true))
            .worldWar(json.bool("WORLD_WAR", true))
            .worldPopup(json.bool("WORLD_POPUP", true))
            .worldWarPeace(json.bool("WORLD_WAR_PEACE", true))
            .worldWarPlayer(json.bool("WORLD_WAR_PLAYER", true))
            .build();
    }

    private JsonE mapEvents(MoreOptionsConfig.Events config) {
        JsonE json = new JsonE();
        json.add("ACCIDENT", config.isAccident());
        json.add("ADVICE", config.isAdvice());
        json.add("FARM", config.isFarm());
        json.add("FISH", config.isFish());
        json.add("RIOT", config.isRiot());
        json.add("KILLER", config.isKiller());
        json.add("ORCHARD", config.isOrchard());
        json.add("PASTURE", config.isPasture());
        json.add("DISEASE", config.isDisease());
        json.add("SLAVER", config.isSlaver());
        json.add("RACE_WARS", config.isRaceWars());
        json.add("UPRISING", config.isUprising());
        json.add("WORLD_FACTION_BREAK", config.isWorldFactionBreak());
        json.add("WORLD_RAIDER", config.isWorldRaider());
        json.add("WORLD_FACTION_EXPAND", config.isWorldFactionExpand());
        json.add("WORLD_REBELLION", config.isWorldRebellion());
        json.add("WORLD_WAR", config.isWorldWar());
        json.add("WORLD_POPUP", config.isWorldPopup());
        json.add("WORLD_WAR_PEACE", config.isWorldWarPeace());
        json.add("WORLD_WAR_PLAYER", config.isWorldWarPlayer());

        return json;
    }
}
