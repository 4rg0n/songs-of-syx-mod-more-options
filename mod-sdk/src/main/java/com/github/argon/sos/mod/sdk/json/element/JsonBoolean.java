package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of a boolean in json.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonBoolean implements JsonElement {
    private final Boolean value;

    /**
     * Transforms boolean to a string.
     *
     * @return boolean as string
     */
    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    /**
     * Creates a new {@link JsonBoolean} with given value.
     *
     * @param value boolean value
     * @return created json boolean
     */
    public static JsonBoolean of(Boolean value) {
        return new JsonBoolean(value);
    }

    /**
     * {@inheritDoc}
     */
    public JsonBoolean copy() {
        return JsonBoolean.of(getValue());
    }
}
