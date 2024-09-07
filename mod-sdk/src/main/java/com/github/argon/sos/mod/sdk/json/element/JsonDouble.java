package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonDouble implements JsonElement {
    private final Double value;

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public static JsonDouble of(Double value) {
        return new JsonDouble(value);
    }
    public static JsonDouble of(JsonLong value) {
        return new JsonDouble(value.getValue().doubleValue());
    }

    public static JsonDouble of(Long value) {
        return new JsonDouble(value.doubleValue());
    }

    public JsonDouble copy() {
        return JsonDouble.of(getValue());
    }
}
