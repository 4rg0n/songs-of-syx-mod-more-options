package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.element.JsonArray;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.json.element.JsonTuple;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Would read the value in the NAME of the second entry in STATS.
 */
public class JsonPath {
    private final static Logger log = Loggers.getLogger(JsonPath.class);

    @Getter
    private final List<String> keys;

    /**
     * Used for splitting the json path
     */
    public final static String DELIMITER = "\\.";

    /**
     * Creates a new {@link JsonPath} with given keys describing the path to the value.
     *
     * @param keys describing the path to the value
     */
    public JsonPath(List<String> keys) {
        this.keys = keys;
    }

    /**
     * Returns the last key in the list.
     *
     * @return last key
     */
    public String lastKey() {
        return keys.getLast();
    }

    /**
     * Returns the size of the key list.
     *
     * @return size of key list
     */
    public int size() {
        return keys.size();
    }

    /**
     * Returns the key on given index.
     *
     * @param index of the key
     * @return key for index or null when index is out of bound
     */
    @Nullable
    public String nextKey(int index) {
        if (index + 1 > keys.size() - 1) {
            return null;
        }

        return keys.get(index + 1);
    }

    /**
     * Extracts the {@link JsonElement} from a {@link JsonObject} by using the stored key list as a path.
     *
     * @param json to extract the json element from
     * @return extracted json element
     */
    public Optional<JsonElement> of(JsonObject json) {
        return of(json, JsonElement.class);
    }

    /**
     * Extracts the {@link JsonElement} from a {@link JsonObject} by using the stored key list as a path.
     * The {@link JsonElement} will be cast to the given class type.
     *
     * @param json to extract the value from
     * @param clazz of the json element
     * @return extracted json element
     * @param <T> type of the json element to extract
     */
    public <T extends JsonElement> Optional<T> of(JsonObject json, Class<T> clazz) {
        return of(json, clazz, 0, getKeys().size(), true);
    }

    private <T extends JsonElement> Optional<T> of(JsonObject json, Class<T> clazz, int pos, int maxPos, boolean extractTuple) {
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
                    element = of((JsonObject) element, clazz, i, maxPos, extractTuple).orElse(null);
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

    /**
     * Writes a {@link JsonElement} into the given {@link JsonObject}.
     *
     * @param json to write into
     * @param value to write into json
     */
    public void put(JsonObject json, JsonElement value) {
        resolveContainer(json).ifPresent(container -> writeInto(container, value));
    }

    /**
     * Creates a new {@link JsonPath} from given path.
     * Example: <pre>JsonPath.of("PROPERTIES.NAME");</pre>
     *
     * @param path string to create the json path from
     * @return created json path
     */
    public static JsonPath of(String path) {
        String[] parts = path.split(DELIMITER);

        return new JsonPath(List.of(parts));
    }

    /**
     * Will transform the keys back to a string json path.
     * Will not use indexes.
     *
     * Example: <pre>TECH[0]</pre> would be instead <pre>TECH</pre>
     *
     * @return json path string without e.g. [0]
     */
    public String toStringWithoutIndexes() {
        return String.join(".", getKeysWithoutIndexes());
    }

    /**
     * Will transform the keys back to a string json path.
     * Example: <pre>TECH[0]</pre> or <pre>PROPERTIES.NAME</pre>
     *
     * @return json path string
     */
    @Override
    public String toString() {
        return String.join(".", keys);
    }

    private List<String> getKeysWithoutIndexes() {
        return keys.stream()
            .map(s -> {
                String key = parseKey(s);

                if (key != null) {
                    return key;
                }

                return s;
            }).toList();
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

    /**
     * Returns the container the {@link #lastKey()} should be written into. For single-key
     * paths the container is the root {@code json}; for nested paths it is the element
     * pointed to by all but the last key.
     */
    private Optional<JsonElement> resolveContainer(JsonObject json) {
        if (getKeys().size() <= 1) {
            return Optional.of(json);
        }
        return of(json, JsonElement.class, 0, getKeys().size() - 1, false);
    }

    private void writeInto(JsonElement container, JsonElement value) {
        String lastKey = lastKey();
        if (container instanceof JsonTuple) {
            ((JsonTuple) container).setValue(value);
        } else if (container instanceof JsonArray) {
            Integer index = parseIndex(lastKey);
            if (index != null) {
                ((JsonArray) container).add(index, value);
            }
        } else if (container instanceof JsonObject) {
            ((JsonObject) container).put(lastKey, value);
        }
    }
}
