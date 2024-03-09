package com.github.argon.sos.moreoptions.game.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
    public static Map<String, Boolean> mapBoolean(Json json, boolean fallback) {
        Map<String, Boolean> map = new HashMap<>();

        for (String key : json.keys()) {
            boolean value = json.bool(key, fallback);
            map.put(key, value);
        }

        return map;
    }

    public static JsonE mapBoolean(Map<String, Boolean> map) {
        JsonE json = new JsonE();
        map.forEach(json::add);

        return json;
    }

    public static Map<String, Integer> mapInteger(Json json) {
        Map<String, Integer> map = new HashMap<>();

        for (String key : json.keys()) {
            int value = json.i(key);
            map.put(key, value);
        }

        return map;
    }

    public static JsonE mapInteger(Map<String, Integer> map) {
        JsonE json = new JsonE();
        map.forEach(json::add);

        return json;
    }

    public static JsonE mapString(Map<String, String> map) {
        JsonE json = new JsonE();
        map.forEach(json::add);

        return json;
    }
}
