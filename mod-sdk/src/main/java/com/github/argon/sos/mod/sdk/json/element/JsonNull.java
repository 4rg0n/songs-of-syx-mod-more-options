package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;

/**
 * Representation of a long in json.
 */
@EqualsAndHashCode
public class JsonNull implements JsonElement {
    /**
     * Transforms the {@link JsonNull} element to a {@link String}
     *
     * @return null
     */
    @Override
    public String toString() {
        return "null";
    }

    /**
     * Creates a new {@link JsonNull} element.
     *
     * @return created json null element
     */
    public static JsonNull of() {
        return new JsonNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonNull copy() {
        return JsonNull.of();
    }
}
