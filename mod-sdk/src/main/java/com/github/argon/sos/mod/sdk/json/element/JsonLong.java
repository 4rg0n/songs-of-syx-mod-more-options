package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of a long in json.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonLong implements JsonElement {
    private final Long value;

    /**
     * Transforms the long value to a string
     *
     * @return long value as string
     */
    @Override
    public String toString() {
        return Long.toString(value);
    }

    /**
     * Creates a new {@link JsonLong} from a {@link Long} value.
     *
     * @param value to create the json long with
     * @return created json long
     */
    public static JsonLong of(Long value) {
        return new JsonLong(value);
    }

    /**
     * Creates a new {@link JsonLong} from a {@link Integer} value.
     *
     * @param value to create the json long with
     * @return created json long
     */
    public static JsonLong of(Integer value) {
        return new JsonLong(value.longValue());
    }

    /**
     * Creates a new {@link JsonLong} from a {@link JsonDouble} element.
     *
     * @param value to create the json long with
     * @return created json long
     */
    public static JsonLong of(JsonDouble value) {
        return new JsonLong(value.getValue().longValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonLong copy() {
        return JsonLong.of(getValue());
    }
}
