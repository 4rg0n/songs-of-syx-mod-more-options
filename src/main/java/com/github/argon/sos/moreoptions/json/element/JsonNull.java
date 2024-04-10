package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JsonNull implements JsonElement {
    @Override
    public String toString() {
        return "null";
    }

    public static JsonNull of() {
        return new JsonNull();
    }

    public JsonNull copy() {
        return JsonNull.of();
    }
}
