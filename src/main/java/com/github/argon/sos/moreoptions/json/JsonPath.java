package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class JsonPath {
    @Getter
    private final List<String> keys;

    private final static String DELIMITER = "\\.";

    public JsonPath(List<String> keys) {
        this.keys = keys;
    }

    public String last() {
        return keys.get(keys.size() - 1);
    }

    public Optional<JsonElement> extract(JsonObject json) {
        return extract(json, JsonElement.class);
    }

    public <T extends JsonElement> Optional<T> extract(JsonObject json, Class<T> clazz) {
        JsonElement element = null;

        for (String key : getKeys()) {
            if (json.containsKey(key)) {
                element = json.get(key);

                if (element instanceof JsonObject) {
                    json = (JsonObject) element;
                    element = extract(json, clazz).orElse(null);
                }
            }
        }

        try {
            return Optional.ofNullable(element)
                .filter(clazz::isInstance)
                .map(clazz::cast);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void put(JsonObject json, JsonElement value) {
        extract(json).ifPresent(jsonElement -> {
            if (jsonElement instanceof JsonObject) {
                ((JsonObject) jsonElement).put(last(), value);
            }
        });
    }

    public static JsonPath get(String path) {
        String[] parts = path.split(DELIMITER);

        return new JsonPath(Lists.of(parts));
    }
}
