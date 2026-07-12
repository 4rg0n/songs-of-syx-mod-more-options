package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Representation of a double value in json.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonDouble implements JsonElement {
    private final Double value;

    /**
     * Transforms the double value to a string
     *
     * @return double value as string
     */
    @Override
    public String toString() {
        return Double.toString(value);
    }

    /**
     * Creates a new {@link JsonDouble} from a {@link Double} value.
     *
     * @param value to create the json double with
     * @return created json double
     */
    public static JsonDouble of(Double value) {
        return new JsonDouble(value);
    }

    /**
     * Creates a new {@link JsonDouble} from a {@link JsonLong} element.
     *
     * @param value to create the json double with
     * @return created json double
     */
    public static JsonDouble of(JsonLong value) {
        return new JsonDouble(value.getValue().doubleValue());
    }

    /**
     * Creates a new {@link JsonDouble} from a {@link Long} value.
     *
     * @param value to create the json double with
     * @return created json double
     */
    public static JsonDouble of(Long value) {
        return new JsonDouble(value.doubleValue());
    }

    /**
     * Creates a new {@link JsonDouble} from a {@link Integer} value.
     *
     * @param value to create the json double with
     * @return created json double
     */
    public static JsonDouble of(Integer value) {
        return new JsonDouble(value.doubleValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonDouble copy() {
        return JsonDouble.of(getValue());
    }
}
