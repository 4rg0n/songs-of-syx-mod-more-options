package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.util.JsonMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMapper {

    @Getter(lazy = true)
    private final static ConfigMapper instance = new ConfigMapper();

    public MoreOptionsConfig mapV1(Path path, Json json, MoreOptionsConfig defaultConfig) {
        return MoreOptionsConfig.builder()
            .filePath(path)
            .version(2)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)
            .factionWarAdd((json.has("FACTION_WAR_ADD")) ? mapRange(json.i("FACTION_WAR_ADD", -100, 100, 0), (defaultConfig != null) ? defaultConfig.getFactionWarAdd() : MoreOptionsConfig.Range.builder().build())
                : (defaultConfig != null) ? defaultConfig.getFactionWarAdd() : null)

            .eventsWorld((json.has("EVENTS_WORLD")) ? JsonMapper.mapBoolean(json.json("EVENTS_WORLD"), true)
                : (defaultConfig != null) ? defaultConfig.getEventsWorld() : new HashMap<>())

            .eventsSettlement((json.has("EVENTS_SETTLEMENT")) ? JsonMapper.mapBoolean(json.json("EVENTS_SETTLEMENT"), true)
                : (defaultConfig != null) ? defaultConfig.getEventsSettlement() : new HashMap<>())

            .eventsChance((json.has("EVENTS_CHANCE")) ? JsonMapper.mapInteger(json.json("EVENTS_CHANCE")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getEventsChance().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getEventsChance() : new HashMap<>())

            .soundsAmbience((json.has("SOUNDS_AMBIENCE")) ? JsonMapper.mapInteger(json.json("SOUNDS_AMBIENCE")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getSoundsAmbience().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getSoundsAmbience() : new HashMap<>())

            .soundsSettlement((json.has("SOUNDS_SETTLEMENT")) ? JsonMapper.mapInteger(json.json("SOUNDS_SETTLEMENT")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getSoundsSettlement().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getSoundsSettlement() : new HashMap<>())

            .soundsRoom((json.has("SOUNDS_ROOM")) ? JsonMapper.mapInteger(json.json("SOUNDS_ROOM")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getSoundsRoom().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getSoundsRoom() : new HashMap<>())

            .weather((json.has("WEATHER")) ? JsonMapper.mapInteger(json.json("WEATHER")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getWeather().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getWeather() : new HashMap<>())

            .boosters((json.has("BOOSTERS")) ? JsonMapper.mapInteger(json.json("BOOSTERS")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapRange(entry.getValue(), (defaultConfig != null) ? defaultConfig.getBoosters().get(entry.getKey()) : null)
                )) : (defaultConfig != null) ? defaultConfig.getBoosters() : new HashMap<>())
            .build();
    }

    public MoreOptionsConfig mapV2(Path path, int version, Json json, MoreOptionsConfig defaultConfig) {
        return MoreOptionsConfig.builder()
            .filePath(path)
            .version(version)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)
            .factionWarAdd((json.has("FACTION_WAR_ADD")) ? mapRange(json.json("FACTION_WAR_ADD"), (defaultConfig != null) ? defaultConfig.getFactionWarAdd().getValue() : null)
                : (defaultConfig != null) ? defaultConfig.getFactionWarAdd() : null)

            .eventsWorld((json.has("EVENTS_WORLD")) ? JsonMapper.mapBoolean(json.json("EVENTS_WORLD"), true)
                : (defaultConfig != null) ? defaultConfig.getEventsWorld() : new HashMap<>())

            .eventsSettlement((json.has("EVENTS_SETTLEMENT")) ? JsonMapper.mapBoolean(json.json("EVENTS_SETTLEMENT"), true)
                : (defaultConfig != null) ? defaultConfig.getEventsSettlement() : new HashMap<>())

            .eventsChance((json.has("EVENTS_CHANCE")) ? mapRanges(json.json("EVENTS_CHANCE"))
                : (defaultConfig != null) ? defaultConfig.getEventsChance() : new HashMap<>())

            .soundsAmbience((json.has("SOUNDS_AMBIENCE")) ? mapRanges(json.json("SOUNDS_AMBIENCE"))
                : (defaultConfig != null) ? defaultConfig.getSoundsAmbience() : new HashMap<>())

            .soundsSettlement((json.has("SOUNDS_SETTLEMENT")) ? mapRanges(json.json("SOUNDS_SETTLEMENT"))
                : (defaultConfig != null) ? defaultConfig.getSoundsSettlement() : new HashMap<>())

            .soundsRoom((json.has("SOUNDS_ROOM")) ? mapRanges(json.json("SOUNDS_ROOM"))
                : (defaultConfig != null) ? defaultConfig.getSoundsRoom() : new HashMap<>())

            .weather((json.has("WEATHER")) ? mapRanges(json.json("WEATHER"))
                : (defaultConfig != null) ? defaultConfig.getWeather() : new HashMap<>())

            .boosters((json.has("BOOSTERS")) ? mapRanges(json.json("BOOSTERS"))
                : (defaultConfig != null) ? defaultConfig.getBoosters() : new HashMap<>())
            .build();
    }

    public JsonE mapConfig(MoreOptionsConfig config) {
        JsonE configJson = new JsonE();
        configJson.add("VERSION", config.getVersion());
        configJson.addString("LOG_LEVEL", config.getLogLevel().getName());
        configJson.add("FACTION_WAR_ADD", mapRange(config.getFactionWarAdd(), null));
        configJson.add("EVENTS_SETTLEMENT", JsonMapper.mapBoolean(config.getEventsSettlement()));
        configJson.add("EVENTS_WORLD", JsonMapper.mapBoolean(config.getEventsWorld()));
        configJson.add("EVENTS_CHANCE", mapRanges(config.getEventsChance()));
        configJson.add("SOUNDS_AMBIENCE", mapRanges(config.getSoundsAmbience()));
        configJson.add("SOUNDS_SETTLEMENT", mapRanges(config.getSoundsSettlement()));
        configJson.add("SOUNDS_ROOM", mapRanges(config.getSoundsRoom()));
        configJson.add("WEATHER", mapRanges(config.getWeather()));
        configJson.add("BOOSTERS", mapRanges(config.getBoosters()));
        return configJson;
    }

    public MoreOptionsConfig.Meta mapMeta(Json json) {
        return MoreOptionsConfig.Meta.builder()
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO) : Level.INFO)
            .version((json.has("VERSION")) ? json.i("VERSION") : MoreOptionsConfig.VERSION)
            .build();
    }

    public MoreOptionsConfig.Range mapRange(int value, MoreOptionsConfig.Range defaultRange) {
        MoreOptionsConfig.Range range = MoreOptionsConfig.Range.builder()
            .value(value)
            .build();

        if (defaultRange != null) {
            range.setMin(defaultRange.getMin());
            range.setMax(defaultRange.getMax());
            range.setDisplayMode(defaultRange.getDisplayMode());
        }

        return range;
    }

    public Map<String, MoreOptionsConfig.Range> mapRanges(Json json) {
        Map<String, MoreOptionsConfig.Range> map = new HashMap<>();

        for (String key : json.keys()) {
            Json rangeJson = json.json(key);
            MoreOptionsConfig.Range range = mapRange(rangeJson, null);
            map.put(key, range);
        }

        return map;
    }

    public MoreOptionsConfig.Range mapRange(Json rangeJson, Integer defaultValue) {
        return MoreOptionsConfig.Range.builder()
            .value((defaultValue != null) ? defaultValue : rangeJson.i("VALUE"))
            .min(rangeJson.i("MIN"))
            .max(rangeJson.i("MAX"))
            .displayMode(MoreOptionsConfig.Range.DisplayMode
                .valueOf(rangeJson.text("DISPLAY_MODE")))
            .build();
    }

     public JsonE mapRanges(Map<String, MoreOptionsConfig.Range> ranges) {
        JsonE json = new JsonE();

        ranges.forEach((key, range) -> {
            JsonE rangeJson = mapRange(range, null);
            json.add(key, rangeJson);
        });

        return json;
    }

    public JsonE mapRange(MoreOptionsConfig.Range range, Integer defaultValue) {
        JsonE rangeJson = new JsonE();
        rangeJson.add("VALUE", (defaultValue != null) ? defaultValue : range.getValue());
        rangeJson.add("MIN", range.getMin());
        rangeJson.add("MAX", range.getMax());
        rangeJson.add("DISPLAY_MODE", range.getDisplayMode().toString());

        return rangeJson;
    }
}
