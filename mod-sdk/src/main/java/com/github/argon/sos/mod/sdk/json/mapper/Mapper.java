package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * Common class for mapping objects into {@link JsonElement}
 *
 * @param <T> mapping result
 */
public interface Mapper<T extends JsonElement> {
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
     * @param typeInfo for mapping the
     * @return mapped java type
     */
    Object mapJson(T json, TypeInfo<?> typeInfo);

    /**
     * Maps a Java type to a {@link JsonElement}
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped json element
     */
    T mapObject(Object object, TypeInfo<?> typeInfo);
}
