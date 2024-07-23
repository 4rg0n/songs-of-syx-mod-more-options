package com.github.argon.sos.moreoptions.json.util;

import com.github.argon.sos.moreoptions.json.parser.JsonParser;
import com.github.argon.sos.moreoptions.util.MethodUtil;
import com.github.argon.sos.moreoptions.util.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
    /**
     * This exists to make it easier for the {@link JsonParser} to process the games json format
     */
    public static @Nullable String normalizeGameJson(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return jsonString;
        }

        // remove comments
        Pattern regex = Pattern.compile("^\\s*\\*\\*\\s*.*", Pattern.MULTILINE);
        Matcher regexMatcher = regex.matcher(jsonString);
        jsonString = regexMatcher.replaceAll("");

        if (jsonString.charAt(0) != '{') {
            // wrap everything in {}
            jsonString = "{" + jsonString + "}";
        }

        return jsonString;
    }

    /**
     * @return e.g. a method name like getBioFile will be BIO_FILE
     */
    public static String toJsonEKey(Method method) {
        String name = MethodUtil.extractSetterGetterFieldName(method);
        return StringUtil.toScreamingSnakeCase(name);
    }

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
