package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.JsonMapper;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * For saving and loading {@link MoreOptionsConfig} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigService {
    private final static Logger log = Loggers.getLogger(ConfigService.class);

    @Getter(lazy = true)
    private final static ConfigService instance = new ConfigService(
        JsonService.getInstance()
    );

    private final JsonService jsonService;

    public Optional<MoreOptionsConfig> loadConfig(PATH path, String fileName) {
        return loadConfig(path, fileName, null);
    }

    public Optional<MoreOptionsConfig> loadConfig(String fileName, PATH path, MoreOptionsConfig defaultConfig) {
        return loadConfig(path, fileName, defaultConfig);
    }

    public boolean saveConfig(PATH path, String fileName, MoreOptionsConfig config) {
        log.debug("Saving configuration into %s", path.get().toString());
        log.trace("CONFIG: %s", config);

        JsonE configJson = new JsonE();
        configJson.add("VERSION", config.getVersion());
        configJson.add("EVENTS_SETTLEMENT", JsonMapper.mapBoolean(config.getEventsSettlement()));
        configJson.add("EVENTS_WORLD", JsonMapper.mapBoolean(config.getEventsWorld()));
        configJson.add("EVENTS_CHANCE", JsonMapper.mapInteger(config.getEventsChance()));
        configJson.add("SOUNDS_AMBIENCE", JsonMapper.mapInteger(config.getSoundsAmbience()));
        configJson.add("SOUNDS_SETTLEMENT", JsonMapper.mapInteger(config.getSoundsSettlement()));
        configJson.add("SOUNDS_ROOM", JsonMapper.mapInteger(config.getSoundsRoom()));
        configJson.add("WEATHER", JsonMapper.mapInteger(config.getWeather()));
        configJson.add("BOOSTERS", JsonMapper.mapInteger(config.getBoosters()));

        return jsonService.saveJson(configJson, path, fileName);
    }

    public Optional<MoreOptionsConfig> loadConfig(PATH path, String fileName, MoreOptionsConfig defaultConfig) {
        return jsonService.loadJson(path, fileName).map(json ->
            MoreOptionsConfig.builder()
                .version((json.has("VERSION")) ? json.i("VERSION")
                    : MoreOptionsConfig.VERSION)

                .eventsWorld((json.has("EVENTS_WORLD")) ? JsonMapper.mapBoolean(json.json("EVENTS_WORLD"), true)
                    : (defaultConfig != null) ? defaultConfig.getEventsWorld() : new HashMap<>())

                .eventsSettlement((json.has("EVENTS_SETTLEMENT")) ? JsonMapper.mapBoolean(json.json("EVENTS_SETTLEMENT"), true)
                    : (defaultConfig != null) ? defaultConfig.getEventsSettlement() : new HashMap<>())

                .eventsChance((json.has("EVENTS_CHANCE")) ? JsonMapper.mapInteger(json.json("EVENTS_CHANCE"), 0, 1000)
                    : (defaultConfig != null) ? defaultConfig.getEventsChance() : new HashMap<>())

                .soundsAmbience((json.has("SOUNDS_AMBIENCE")) ? JsonMapper.mapInteger(json.json("SOUNDS_AMBIENCE"), 0, 100)
                    : (defaultConfig != null) ? defaultConfig.getSoundsAmbience() : new HashMap<>())

                .soundsSettlement((json.has("SOUNDS_SETTLEMENT")) ? JsonMapper.mapInteger(json.json("SOUNDS_SETTLEMENT"), 0 , 100)
                    : (defaultConfig != null) ? defaultConfig.getSoundsSettlement() : new HashMap<>())

                .soundsRoom((json.has("SOUNDS_ROOM")) ? JsonMapper.mapInteger(json.json("SOUNDS_ROOM"), 0 , 100)
                    : (defaultConfig != null) ? defaultConfig.getSoundsRoom() : new HashMap<>())

                .weather((json.has("WEATHER")) ? JsonMapper.mapInteger(json.json("WEATHER"), 0, 100)
                    : (defaultConfig != null) ? defaultConfig.getWeather() : new HashMap<>())

                .boosters((json.has("BOOSTERS")) ? JsonMapper.mapInteger(json.json("BOOSTERS"), 0, 100)
                    : (defaultConfig != null) ? defaultConfig.getBoosters() : new HashMap<>())
                .build()
        );
    }

    public Optional<Map<String, Dictionary.Entry>> loadDictionary(PATH path, String fileName) {
        return jsonService.loadJson(path, fileName).map(json -> {
            HashMap<String, Dictionary.Entry> dictEntries = new HashMap<>();

            json.keys().forEach(key -> {
                Json jsonDictEntry = json.json(key);

                String title = jsonDictEntry.text("TITLE", key);
                String description = jsonDictEntry.text("DESC", null);

                dictEntries.put(key, Dictionary.Entry.builder()
                    .key(key)
                    .title(title)
                    .description(description)
                    .build());
            });

            return dictEntries;
        });
    }

    public boolean saveDictionary(Map<String, Dictionary.Entry> entries, PATH path, String fileName) {
        JsonE jsonEntries = new JsonE();

        entries.forEach((key, entry) -> {
            JsonE jsonDictEntry = new JsonE();
            jsonDictEntry.addString("TITLE", entry.getTitle());

            if (entry.getDescription() != null) {
                jsonDictEntry.addString("DESC", entry.getDescription());
            }

            jsonEntries.add(key, jsonDictEntry);
        });

        return jsonService.saveJson(jsonEntries, path, fileName);
    }
}
