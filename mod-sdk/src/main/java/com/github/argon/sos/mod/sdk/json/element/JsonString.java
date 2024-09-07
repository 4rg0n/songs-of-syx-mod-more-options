package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonString implements JsonElement {
    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static JsonString of(String value) {
        return new JsonString(value);
    }

    public JsonString copy() {
        return JsonString.of(getValue());
    }
}
