package com.github.argon.sos.mod.sdk.json.element;

import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class JsonTuple implements JsonElement {
    private final String key;
    @Setter
    private JsonElement value;

    public JsonTuple copy() {
        return new JsonTuple(key, value.copy());
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }

    public static JsonTuple of(String key, JsonElement element) {
        return new JsonTuple(key, element);
    }
}
