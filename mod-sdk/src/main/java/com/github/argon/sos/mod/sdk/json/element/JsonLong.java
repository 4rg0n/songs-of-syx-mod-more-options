package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonLong implements JsonElement {
    private final Long value;

    @Override
    public String toString() {
        return Long.toString(value);
    }

    public static JsonLong of(long value) {
        return new JsonLong(value);
    }

    public static JsonLong of(int value) {
        return new JsonLong((long) value);
    }

    public static JsonLong of(JsonDouble value) {
        return new JsonLong(value.getValue().longValue());
    }

    public JsonLong copy() {
        return JsonLong.of(getValue());
    }
}
