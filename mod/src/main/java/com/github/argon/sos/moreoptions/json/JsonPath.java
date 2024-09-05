package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.json.element.JsonTuple;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A json path describes a way to find a value in a json tree.
 * e.g. for:
 * <pre>
 *     PROPERTIES: {
 *         WIDTH: 4,
 *     },
 * </pre>
 * The json path to read the 4 would be "PROPERTIES.WIDTH"
 *
 * e.g. for a list:
 * <pre>
 *     TECH: [
 *         *,
 *     ],
 * </pre>
 * The json path for reading the "*" would be "TECH[0]"
 * It's also possible to combine both e.g. "STATS[1].NAME".
 * Would read the value in NAME of the second entry in STATS.
 */
public class JsonPath {
    private final static Logger log = Loggers.getLogger(JsonPath.class);

    @Getter
    private final List<String> keys;

    public final static String DELIMITER = "\\.";

    public JsonPath(List<String> keys) {
        this.keys = keys;
    }

    public String lastKey() {
        return keys.get(keys.size() - 1);
    }

    public int size() {
        return keys.size();
    }

    @Nullable
    public String nextKey(int index) {
        if (index + 1 > keys.size() - 1) {
            return null;
        }

        return keys.get(index + 1);
    }

    public Optional<JsonElement> get(JsonObject json) {
        return get(json, JsonElement.class);
    }

    public <T extends JsonElement> Optional<T> get(JsonObject json, Class<T> clazz) {

        return get(json, clazz, 0, getKeys().size(), true);
    }

    private <T extends JsonElement> Optional<T> get(JsonObject json, Class<T> clazz, int pos, int maxPos, boolean extractTuple) {
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

                    if (extractTuple && element instanceof JsonTuple) {
                        String nextKey = nextKey(i);

                        // only extract value when there is no next key; or when the key matches with the one in tuple
                        if (nextKey == null) {
                            element = ((JsonTuple) element).getValue();
                        } else if (nextKey.equals(((JsonTuple) element).getKey())) {
                            element = ((JsonTuple) element).getValue();
                            i++;
                        } else {
                            return Optional.empty();
                        }
                    }
                }

                if (i < maxPos - 1 && element instanceof JsonObject) {
                    i++;
                    element = get((JsonObject) element, clazz, i, maxPos, extractTuple).orElse(null);
                }
            }
        }



        try {
            return Optional.ofNullable(element)
                .filter(clazz::isInstance)
                .map(clazz::cast);
        } catch (Exception e) {
            log.debug("Could not cast JsonElement %s", toString(), e);
            return Optional.empty();
        }
    }

    public void put(JsonObject json, JsonElement value) {
        get(json, JsonElement.class, 0, getKeys().size() - 1, false).ifPresent(jsonElement -> {
            if (jsonElement instanceof JsonTuple) {
                ((JsonTuple) jsonElement).setValue(value);
            } else if (jsonElement instanceof JsonArray) {
                String lastKey = lastKey();
                Integer index = parseIndex(lastKey);

                if (index != null) {
                    ((JsonArray) jsonElement).add(index, value);
                }
            } else if (jsonElement instanceof JsonObject) {
                String lastKey = lastKey();
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

    public List<String> getKeysWithoutIndexes() {
        return keys.stream()
            .map(s -> {
                String key = parseKey(s);

                if (key != null) {
                    return key;
                }

                return s;
            }).collect(Collectors.toList());
    }

    public String toStringWithoutIndexes() {
        return String.join(".", getKeysWithoutIndexes());
    }



    @Override
    public String toString() {
        return String.join(".", keys);
    }
}
