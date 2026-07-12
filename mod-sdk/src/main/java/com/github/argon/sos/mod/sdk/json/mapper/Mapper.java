package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * Common class for mapping objects into {@link JsonElement}
 *
 * @param <JSON> mapping result
 */
public interface Mapper<JSON extends JsonElement> {
    /**
     * Checks whether this class supports the mapping of given class type.
     *
     * @param clazz to check
     * @return whether the mapper supports mapping the given class
     */
    boolean supports(Class<?> clazz);
    /**
     * Maps a standard {@link JsonElement} to Java types.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     */
    Object mapJson(JSON json, TypeInfo<?> typeInfo);

    /**
     * Maps a Java type to a {@link JsonElement}
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped json element
     */
    JSON mapObject(Object object, TypeInfo<?> typeInfo);
}
