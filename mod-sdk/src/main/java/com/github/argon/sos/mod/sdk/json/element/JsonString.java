package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of a string in json.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonString implements JsonElement {
    private final String value;

    /**
     * Just returns the actually stored string value.
     *
     * @return string value
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Creates a new {@link JsonString} with given value.
     *
     * @param value string value
     * @return created json string
     */
    public static JsonString of(String value) {
        return new JsonString(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonString copy() {
        return JsonString.of(getValue());
    }
}
