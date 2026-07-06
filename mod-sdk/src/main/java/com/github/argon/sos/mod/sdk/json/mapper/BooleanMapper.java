package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonBoolean;
import com.github.argon.sos.mod.sdk.util.ClassUtil;

import java.util.Arrays;

/**
 * For mapping {@link JsonBoolean} to a java {@link Boolean} and visa versa.
 */
public class BooleanMapper implements Mapper<JsonBoolean> {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Arrays.asList(Boolean.class, boolean.class)
            .contains(clazz);
    }

    /**
     * Maps a {@link JsonBoolean} to a {@link Boolean}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     */
    @Override
    public Object mapJson(JsonBoolean json, TypeInfo<?> typeInfo) {
        return json.getValue();
    }

    /**
     * Maps a {@link Boolean} to a {@link JsonBoolean}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped boolean json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonBoolean mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Boolean value) {
            return new JsonBoolean(value);
        } else if (ClassUtil.sameAs(clazz, boolean.class)) {
            boolean value = (boolean) object;
            return new JsonBoolean(value);
        } else {
            throw new JsonMapperException("Can not map " + clazz.getTypeName() + " to " + JsonBoolean.class.getSimpleName());
        }
    }
}
