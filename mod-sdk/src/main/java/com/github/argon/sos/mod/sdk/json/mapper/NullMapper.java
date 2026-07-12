package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonNull;
import org.jetbrains.annotations.Nullable;

/**
 * For mapping {@link JsonNull} to a java null and visa versa.
 */
public class NullMapper implements Mapper<JsonNull> {
    /**
     * Creates a new {@link NullMapper}.
     */
    public NullMapper() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == null || clazz == Void.class;
    }

    /**
     * Maps a {@link JsonNull} to a null value.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     */
    @Override
    @Nullable
    public Object mapJson(JsonNull json, TypeInfo<?> typeInfo) {
        return null;
    }

    /**
     * Maps a null value to a {@link JsonNull}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped null json element
     */
    @Override
    public JsonNull mapObject(Object object, TypeInfo<?> typeInfo) {
        return new JsonNull();
    }
}
