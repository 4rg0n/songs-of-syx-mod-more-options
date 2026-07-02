package com.github.argon.sos.mod.sdk.json.element;

import lombok.*;

/**
 * Representation of a key value pair in json.
 * E.g. <pre>NAME: Human</pre>
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class JsonTuple implements JsonElement {
    private final String key;
    @Setter
    private JsonElement value;

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonTuple copy() {
        return new JsonTuple(key, value.copy());
    }

    /**
     * Transforms the stored key and value to a string
     *
     * @return key and value as string
     */
    @Override
    public String toString() {
        return key + ": " + value;
    }

    /**
     * Creates a new {@link JsonTuple} from the given key and element.
     *
     * @param key to use
     * @param element to use
     * @return created json tuple
     */
    public static JsonTuple of(String key, JsonElement element) {
        return new JsonTuple(key, element);
    }
}
