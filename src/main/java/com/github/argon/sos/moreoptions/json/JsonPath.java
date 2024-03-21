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

    public Optional<JsonElement> extract(JsonObject json) {
        JsonElement element = null;
        for (String key : getKeys()) {
            if (json.containsKey(key)) {
                element = json.get(key);

                if (element instanceof JsonObject) {
                    json = (JsonObject) element;
                }
            }
        }

        return Optional.ofNullable(element);
    }

    public static JsonPath get(String path) {
        String[] parts = path.split(DELIMITER);

        return new JsonPath(Lists.of(parts));
    }
}
