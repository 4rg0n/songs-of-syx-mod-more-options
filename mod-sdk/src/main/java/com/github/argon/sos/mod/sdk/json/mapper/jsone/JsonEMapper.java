package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import snake2d.util.file.JsonE;

/**
 * Common mapper class for mapping into the games {@link JsonE}
 *
 * @param <T> {@link JsonElement} to map from
 */
public interface JsonEMapper<T extends JsonElement> {
    /**
     * Maps a standard {@link JsonElement} to the games {@link JsonE}.
     *
     * @param json game json
     * @param key of the json element
     * @param jsonElement to map
     * @return mapped game json object
     */
    JsonE mapJsonE(JsonE json, String key, T jsonElement);

    /**
     * Checks whether this class supports the mapping of given class type.
     *
     * @param clazz
     * @return
     */
    boolean supports(Class<?> clazz);
}
