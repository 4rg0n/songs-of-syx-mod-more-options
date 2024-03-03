package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.util.JsonUtil;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.moreoptions.config.MoreOptionsV2Config.*;

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
        return builder()
            .version(VERSION)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)
            .events(Events.builder()
                .world((json.has("EVENTS_WORLD")) ? JsonUtil.mapBoolean(json.json("EVENTS_WORLD"), true) : null)
                .settlement((json.has("EVENTS_SETTLEMENT")) ? JsonUtil.mapBoolean(json.json("EVENTS_SETTLEMENT"), true) : null)

                .chance((json.has("EVENTS_CHANCE")) ? JsonUtil.mapInteger(json.json("EVENTS_CHANCE")).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ConfigUtil.newRange(entry.getValue())
                    )) : null)
                .build())

            .sounds(Sounds.builder()
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
        return builder()
            .version(VERSION)
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO)
                : Level.INFO)

            .events(Events.builder()
                .world((json.has("EVENTS_WORLD")) ? JsonUtil.mapBoolean(json.json("EVENTS_WORLD"), true) : null)
                .settlement((json.has("EVENTS_SETTLEMENT")) ? JsonUtil.mapBoolean(json.json("EVENTS_SETTLEMENT"), true) : null)
                .chance((json.has("EVENTS_CHANCE")) ? mapRanges(json.json("EVENTS_CHANCE")) : null)
                .build())

            .sounds(Sounds.builder()
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
        JsonE json = new JsonE();
        json.add("VERSION", config.getVersion());
        json.addString("LOG_LEVEL", config.getLogLevel().getName());
        json.add("EVENTS_SETTLEMENT", JsonUtil.mapBoolean(config.getEvents().getSettlement()));
        json.add("EVENTS_WORLD", JsonUtil.mapBoolean(config.getEvents().getWorld()));
        json.add("EVENTS_CHANCE", mapRanges(config.getEvents().getChance()));
        json.add("SOUNDS_AMBIENCE", mapRanges(config.getSounds().getAmbience()));
        json.add("SOUNDS_SETTLEMENT", mapRanges(config.getSounds().getSettlement()));
        json.add("SOUNDS_ROOM", mapRanges(config.getSounds().getRoom()));
        json.add("WEATHER", mapRanges(config.getWeather()));
        json.add("BOOSTERS", mapRanges(config.getBoosters()));
        json.add("METRICS", mapMetrics(config.getMetrics()));
        return json;
    }

    public static JsonE mapMetrics(Metrics metrics) {
        JsonE json = new JsonE();

        json.add("ENABLED", metrics.isEnabled());
        json.add("COLLECTION_RATE_SECONDS", mapRange(metrics.getCollectionRateSeconds(), 15));
        json.add("EXPORT_RATE_MINUTES", mapRange(metrics.getExportRateMinutes(), 30));
        json.addStrings("STATS", metrics.getStats().toArray(new String[]{}));

        return json;
    }

    public static Metrics mapMetrics(Json json) {
        Metrics defaultMetrics = ConfigDefaults.metrics();
        List<String> stats = (json.has("STATS")) ? Arrays.asList(json.texts("STATS")) : defaultMetrics.getStats();
        Collections.sort(stats);

        return Metrics.builder()
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

    public static JsonE mapRacesConfig(RacesConfig config) {
        JsonE json = new JsonE();

        json.add("LIKINGS", mapRaceLikings(config.getLikings()));

        return json;
    }

    public static JsonE[] mapRaceLikings(List<RacesConfig.Liking> likings) {
        JsonE[] jsons = new JsonE[likings.size()];

        for (int i = 0, likingsSize = likings.size(); i < likingsSize; i++) {
            RacesConfig.Liking liking = likings.get(i);
            jsons[i] = mapRaceLiking(liking);
        }

        return jsons;
    }

    public static JsonE mapRaceLiking(RacesConfig.Liking liking) {
        JsonE json = new JsonE();
        json.addString("RACE", liking.getRace());
        json.addString("OTHER_RACE", liking.getOtherRace());
        json.add("RANGE", mapRange(liking.getRange(), 0));

        return json;
    }

    public static RacesConfig mapRacesConfig(Json json) {
        return RacesConfig.builder()
            .likings((json.has("LIKINGS"))
                ? mapRaceLikings(json.jsons("LIKINGS"))
                : new ArrayList<>())
            .build();
    }

    public static List<RacesConfig.Liking> mapRaceLikings(Json[] jsons) {
        List<RacesConfig.Liking> likings = new ArrayList<>();

        for (Json json : jsons) {
            likings.add(mapRaceLiking(json));
        }

        return likings;
    }

    public static RacesConfig.Liking mapRaceLiking(Json json) {
        return RacesConfig.Liking.builder()
            .race(json.has("RACE") ? json.text("RACE") : null)
            .otherRace(json.has("OTHER_RACE") ? json.text("OTHER_RACE") : null)
            .range(json.has("RANGE") ? mapRange(json.json("RANGE"), 0) : null)
            .build();
    }

    public static Meta mapMeta(Json json) {
        return Meta.builder()
            .logLevel((json.has("LOG_LEVEL")) ? Level.fromName(json.text("LOG_LEVEL")).orElse(Level.INFO) : Level.INFO)
            .version((json.has("VERSION")) ? json.i("VERSION") : VERSION)
            .build();
    }

    public static Map<String, Range> mapRanges(Json json) {
        Map<String, Range> map = new HashMap<>();

        for (String key : json.keys()) {
            Json rangeJson = json.json(key);
            Range range = mapRange(rangeJson, null);
            map.put(key, range);
        }

        return map;
    }

    public static Range mapRange(Json rangeJson, @Nullable Integer defaultValue) {
        return Range.builder()
            .value((defaultValue != null) ? defaultValue : rangeJson.i("VALUE"))
            .min(rangeJson.i("MIN"))
            .max(rangeJson.i("MAX"))
            .applyMode(Range.ApplyMode
                .valueOf(rangeJson.text("APPLY_MODE")))
            .displayMode(Range.DisplayMode
                .valueOf(rangeJson.text("DISPLAY_MODE")))
            .build();
    }

     public static JsonE mapRanges(Map<String, Range> ranges) {
        JsonE json = new JsonE();

        ranges.forEach((key, range) -> {
            JsonE rangeJson = mapRange(range, null);
            json.add(key, rangeJson);
        });

        return json;
    }

    public static JsonE mapRange(Range range, @Nullable Integer defaultValue) {
        JsonE json = new JsonE();
        json.add("VALUE", (defaultValue != null) ? defaultValue : range.getValue());
        json.add("MIN", range.getMin());
        json.add("MAX", range.getMax());
        json.addString("APPLY_MODE", range.getApplyMode().toString());
        json.addString("DISPLAY_MODE", range.getDisplayMode().toString());

        return json;
    }
}
