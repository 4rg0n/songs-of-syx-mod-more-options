package com.github.argon.sos.moreoptions.json.element;

import com.github.argon.sos.moreoptions.util.ClassUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class JsonObject implements JsonElement {

    @Getter
    private final Map<String, JsonElement> map = new HashMap<>();

    public JsonElement get(String key) {
        return map.get(key);
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public JsonElement put(String key, JsonElement value) {
        return map.put(key, value);
    }

    public JsonElement put(Enum<?> key, JsonElement value) {
        return map.put(key.name(), value);
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public <T extends JsonElement> Optional<T> getAs(String key, Class<T> clazz) {
        JsonElement jsonElement = get(key);

        if (!ClassUtil.instanceOf(jsonElement, clazz)) {
            return Optional.empty();
        }

        try {
            return Optional.of(clazz.cast(jsonElement));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return map.keySet().stream()
            .map(key -> key + ": " + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }
}
