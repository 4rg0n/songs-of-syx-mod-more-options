package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonBoolean implements JsonElement {
    private final Boolean value;

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    public static JsonBoolean of(Boolean value) {
        return new JsonBoolean(value);
    }
}
