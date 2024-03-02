package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.util.JsonUtil;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO outsource map into logic?
 */
public class ConfigMapper {

    /**
     * Maps old V1 config to the current config structure
     *
     * @param json with config data
     */
    public static MoreOptionsV2Config mapV1(Json json) {
        return MoreOptionsV2Config.builder()
            .version(MoreOptionsV2Config.VERSION)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)
            .events(MoreOptionsV2Config.Events.builder()
                .world((json.has("EVENTS_WORLD")) ? JsonUtil.mapBoolean(json.json("EVENTS_WORLD"), true) : null)
                .settlement((json.has("EVENTS_SETTLEMENT")) ? JsonUtil.mapBoolean(json.json("EVENTS_SETTLEMENT"), true) : null)

                .chance((json.has("EVENTS_CHANCE")) ? JsonUtil.mapInteger(json.json("EVENTS_CHANCE")).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigUtil.newRange(entry.getValue())
                    )) : null)
                .build())

            .sounds(MoreOptionsV2Config.Sounds.builder()
                .ambience((json.has("SOUNDS_AMBIENCE")) ? JsonUtil.mapInteger(json.json("SOUNDS_AMBIENCE")).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigUtil.newRange(entry.getValue())
                    )) : null)

                .settlement((json.has("SOUNDS_SETTLEMENT")) ? JsonUtil.mapInteger(json.json("SOUNDS_SETTLEMENT")).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigUtil.newRange(entry.getValue())
                    )) : null)

                .room((json.has("SOUNDS_ROOM")) ? JsonUtil.mapInteger(json.json("SOUNDS_ROOM")).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigUtil.newRange(entry.getValue())
                    )) : null)
                .build())

            .weather((json.has("WEATHER")) ? JsonUtil.mapInteger(json.json("WEATHER")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> ConfigUtil.newRange(entry.getValue())
                )) : null)

            .boosters((json.has("BOOSTERS")) ? JsonUtil.mapInteger(json.json("BOOSTERS")).entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> ConfigUtil.newRange(entry.getValue())
                )) : null)
            .build();
    }

    /**
     * Maps V2 config to the current config structure
     */
    public static MoreOptionsV2Config mapV2(Json json) {
        return MoreOptionsV2Config.builder()
            .version(MoreOptionsV2Config.VERSION)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)

            .events(MoreOptionsV2Config.Events.builder()
                .world((json.has("EVENTS_WORLD")) ? JsonUtil.mapBoolean(json.json("EVENTS_WORLD"), true) : null)
                .settlement((json.has("EVENTS_SETTLEMENT")) ? JsonUtil.mapBoolean(json.json("EVENTS_SETTLEMENT"), true) : null)
                .chance((json.has("EVENTS_CHANCE")) ? mapRanges(json.json("EVENTS_CHANCE")) : null)
                .build())

            .sounds(MoreOptionsV2Config.Sounds.builder()
                .ambience((json.has("SOUNDS_AMBIENCE")) ? mapRanges(json.json("SOUNDS_AMBIENCE")) : null)

                .settlement((json.has("SOUNDS_SETTLEMENT")) ? mapRanges(json.json("SOUNDS_SETTLEMENT")) : null)

                .room((json.has("SOUNDS_ROOM")) ? mapRanges(json.json("SOUNDS_ROOM")) : null)
                .build())

            .weather((json.has("WEATHER")) ? mapRanges(json.json("WEATHER")) : null)

            .boosters((json.has("BOOSTERS")) ? mapRanges(json.json("BOOSTERS")) : null)

            .metrics((json.has("METRICS")) ? mapMetrics(json.json("METRICS")) : null)
            // todo
//            .races((json.has("RACES")) ? mapMetrics(json.json("RACES")) : null)

            .build();
    }

    public static JsonE mapConfig(MoreOptionsV2Config config) {
        JsonE configJson = new JsonE();
        configJson.add("VERSION", config.getVersion());
        configJson.addString("LOG_LEVEL", config.getLogLevel().getName());
        configJson.add("EVENTS_SETTLEMENT", JsonUtil.mapBoolean(config.getEvents().getSettlement()));
        configJson.add("EVENTS_WORLD", JsonUtil.mapBoolean(config.getEvents().getWorld()));
        configJson.add("EVENTS_CHANCE", mapRanges(config.getEvents().getChance()));
        configJson.add("SOUNDS_AMBIENCE", mapRanges(config.getSounds().getAmbience()));
        configJson.add("SOUNDS_SETTLEMENT", mapRanges(config.getSounds().getSettlement()));
        configJson.add("SOUNDS_ROOM", mapRanges(config.getSounds().getRoom()));
        configJson.add("WEATHER", mapRanges(config.getWeather()));
        configJson.add("BOOSTERS", mapRanges(config.getBoosters()));
        configJson.add("METRICS", mapMetrics(config.getMetrics()));
        return configJson;
    }

    public static JsonE mapMetrics(MoreOptionsV2Config.Metrics metrics) {
        JsonE metricsJson = new JsonE();

        metricsJson.add("ENABLED", metrics.isEnabled());
        metricsJson.add("COLLECTION_RATE_SECONDS", mapRange(metrics.getCollectionRateSeconds(), 15));
        metricsJson.add("EXPORT_RATE_MINUTES", mapRange(metrics.getExportRateMinutes(), 30));
        metricsJson.addStrings("STATS", metrics.getStats().toArray(new String[]{}));

        return metricsJson;
    }

    public static MoreOptionsV2Config.Metrics mapMetrics(Json json) {
        MoreOptionsV2Config.Metrics defaultMetrics = MoreOptionsV2Config.Metrics.builder().build();
        List<String> stats = (json.has("STATS")) ? Arrays.asList(json.texts("STATS")) : defaultMetrics.getStats();
        Collections.sort(stats);

        return MoreOptionsV2Config.Metrics.builder()
            .enabled((json.has("ENABLED"))
                ? json.bool("ENABLED")
                : defaultMetrics.isEnabled())
            .collectionRateSeconds((json.has("COLLECTION_RATE_SECONDS"))
                ? mapRange(json.json("COLLECTION_RATE_SECONDS"), defaultMetrics.getCollectionRateSeconds().getValue())
                : defaultMetrics.getCollectionRateSeconds())
            .exportRateMinutes((json.has("EXPORT_RATE_MINUTES"))
                ? mapRange(json.json("EXPORT_RATE_MINUTES"), defaultMetrics.getExportRateMinutes().getValue())
                : defaultMetrics.getExportRateMinutes())
            .stats(stats)
            .build();
    }

    public static MoreOptionsV2Config.Meta mapMeta(Json json) {
        return MoreOptionsV2Config.Meta.builder()
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO) : Level.INFO)
            .version((json.has("VERSION")) ? json.i("VERSION") : MoreOptionsV2Config.VERSION)
            .build();
    }

    public static Map<String, MoreOptionsV2Config.Range> mapRanges(Json json) {
        Map<String, MoreOptionsV2Config.Range> map = new HashMap<>();

        for (String key : json.keys()) {
            Json rangeJson = json.json(key);
            MoreOptionsV2Config.Range range = mapRange(rangeJson, null);
            map.put(key, range);
        }

        return map;
    }

    public static MoreOptionsV2Config.Range mapRange(Json rangeJson, @Nullable Integer defaultValue) {
        return MoreOptionsV2Config.Range.builder()
            .value((defaultValue != null) ? defaultValue : rangeJson.i("VALUE"))
            .min(rangeJson.i("MIN"))
            .max(rangeJson.i("MAX"))
            .applyMode(MoreOptionsV2Config.Range.ApplyMode
                .valueOf(rangeJson.text("APPLY_MODE")))
            .displayMode(MoreOptionsV2Config.Range.DisplayMode
                .valueOf(rangeJson.text("DISPLAY_MODE")))
            .build();
    }

     public static JsonE mapRanges(Map<String, MoreOptionsV2Config.Range> ranges) {
        JsonE json = new JsonE();

        ranges.forEach((key, range) -> {
            JsonE rangeJson = mapRange(range, null);
            json.add(key, rangeJson);
        });

        return json;
    }

    public static JsonE mapRange(MoreOptionsV2Config.Range range, @Nullable Integer defaultValue) {
        JsonE rangeJson = new JsonE();
        rangeJson.add("VALUE", (defaultValue != null) ? defaultValue : range.getValue());
        rangeJson.add("MIN", range.getMin());
        rangeJson.add("MAX", range.getMax());
        rangeJson.addString("APPLY_MODE", range.getApplyMode().toString());
        rangeJson.addString("DISPLAY_MODE", range.getDisplayMode().toString());

        return rangeJson;
    }
}
