package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.Json;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonPath {
    private final static Logger log = Loggers.getLogger(JsonPath.class);

    @Getter
    private final List<String> keys;

    public final static String DELIMITER = "\\.";

    public JsonPath(List<String> keys) {
        this.keys = keys;
    }

    public String last() {
        return keys.get(keys.size() - 1);
    }

    public Optional<Json> extractJsonE(Json json) {
        return extractJsonE(json, 0);
    }

    private Optional<Json> extractJsonE(Json json, int pos) {
        Json element = null;

        List<String> jsonKeys = getKeys();
        for (int i = pos, stringsSize = jsonKeys.size(); i < stringsSize; i++) {
            String key = jsonKeys.get(i);
            if (json.has(key)) {
                if (i < stringsSize - 1 && json.jsonIs(key)) {
                    element = extractJsonE(json.json(key), i).orElse(null);
                } else if (json.jsonIs(key)){
                    element = json.json(key);
                }
            }
        }

        return Optional.ofNullable(element);
    }

    public Optional<JsonElement> get(JsonObject json) {
        return get(json, JsonElement.class);
    }

    public <T extends JsonElement> Optional<T> get(JsonObject json, Class<T> clazz) {
        return get(json, clazz, 0, getKeys().size());
    }

    private  <T extends JsonElement> Optional<T> get(JsonObject json, Class<T> clazz, int pos, int maxPos) {
        JsonElement element = null;
        List<String> jsonKeys = getKeys();

        for (int i = pos; i < maxPos; i++) {
            String key = jsonKeys.get(i);
            Integer index = parseIndex(key);
            String arrayKey = parseKey(key);

            // ARRAY
            if (arrayKey != null) {
                key = arrayKey;
            }

            if (json.containsKey(key)) {
                element = json.get(key);

                if (element instanceof JsonArray && index != null) {
                    JsonArray jsonArray = (JsonArray) element;

                    // out of bound?
                    if (index > jsonArray.size() - 1) {
                        return Optional.empty();
                    }

                    element = (jsonArray).get(index);
                }

                if (i < maxPos - 1 && element instanceof JsonObject) {
                    json = (JsonObject) element;
                    element = get(json, clazz, i, maxPos).orElse(null);
                }
            }
        }

        try {
            return Optional.ofNullable(element)
                .map(jsonElement -> {
                    // convert JsonDouble and JsonLong
                    if (ClassUtil.instanceOf(clazz, JsonDouble.class) && jsonElement instanceof JsonLong) {
                        return JsonDouble.of((JsonLong) jsonElement);
                    } else if (ClassUtil.instanceOf(clazz, JsonLong.class) && jsonElement instanceof JsonDouble) {
                        return JsonLong.of((JsonDouble) jsonElement);
                    }

                    return jsonElement;
                })
                .filter(clazz::isInstance)
                .map(clazz::cast);
        } catch (Exception e) {
            log.debug("Could not cast JsonElement %s", toString(), e);
            return Optional.empty();
        }
    }

    public void put(JsonObject json, JsonElement value) {
        get(json, JsonElement.class, 0, getKeys().size() - 1).ifPresent(jsonElement -> {
            if (jsonElement instanceof JsonArray) {
                String lastKey = last();
                Integer index = parseIndex(lastKey);

                if (index != null) {
                    ((JsonArray) jsonElement).add(index, value);
                }
            } else if (jsonElement instanceof JsonObject) {
                String lastKey = last();
                ((JsonObject) jsonElement).put(lastKey, value);
            }
        });
    }

    public static JsonPath get(String path) {
        String[] parts = path.split(DELIMITER);

        return new JsonPath(Lists.of(parts));
    }

    @Nullable
    private Integer parseIndex(String key) {
        Pattern p = Pattern.compile(".+\\[(\\d+)]");
        Matcher m = p.matcher(key);

        if (!m.matches()) {
            return null;
        }

        try {
            return Integer.parseInt(m.group(1));
        } catch (Exception e) {
            log.debug("Could not parse integer %s", toString(), e);
            return null;
        }
    }

    @Nullable
    private String parseKey(String key) {
        Pattern p = Pattern.compile("(.+)\\[\\d+]");
        Matcher m = p.matcher(key);

        if (!m.matches()) {
            return null;
        }

        try {
            return m.group(1);
        } catch (Exception e) {
            log.debug("Could not parse key from %s %s", key, toString(), e);
            return null;
        }
    }

    @Override
    public String toString() {
        return String.join(".", keys);
    }
}
