package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * Common class for mapping objects into {@link JsonElement}
 *
 * @param <T> mapping result
 */
public interface Mapper<T extends JsonElement> {
    /**
     * @param clazz nullable
     */
    boolean supports(Class<?> clazz);
    Object mapJson(T json, TypeInfo<?> typeInfo);
    T mapObject(Object object, TypeInfo<?> typeInfo);
}
