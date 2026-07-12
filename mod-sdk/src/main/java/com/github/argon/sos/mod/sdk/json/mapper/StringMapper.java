package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonString;
import com.github.argon.sos.mod.sdk.util.ClassUtil;

/**
 * For mapping {@link JsonString} to a java {@link String} and visa versa.
 */
public class StringMapper implements Mapper<JsonString> {
    /**
     * Creates a new {@link StringMapper}.
     */
    public StringMapper() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, CharSequence.class);
    }

    /**
     * Maps a {@link JsonString} to a {@link String}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonString json, TypeInfo<?> typeInfo) {
        Class<?> typeClass = typeInfo.getTypeClass();

        if (ClassUtil.instanceOf(typeClass, CharSequence.class)) {
            return json.getValue();
        } else {
            throw new JsonMapperException("Can not map " + JsonString.class.getSimpleName() + " to type " + typeInfo.getType().getTypeName());
        }
    }

    /**
     * Maps a {@link String} to a {@link JsonString}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped string json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonString mapObject(Object object, TypeInfo<?> typeInfo) {
        if (object instanceof CharSequence) {
            return new JsonString((String) object);
        } else {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " +  JsonString.class.getSimpleName());
        }
    }
}
