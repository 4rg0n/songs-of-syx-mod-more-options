package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonString;
import com.github.argon.sos.mod.sdk.util.ClassCastUtil;

/**
 * For mapping {@link JsonString} to a java {@link Enum} and visa versa.
 */
public class EnumMapper implements Mapper<JsonString> {
    /**
     * Creates a new {@link EnumMapper}.
     */
    public EnumMapper() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return clazz.isEnum();
    }

    /**
     * Maps a {@link JsonString} to an {@link Enum}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonString json, TypeInfo<?> typeInfo) {
        Class<?> typeClass = typeInfo.getTypeClass();

        if (typeClass.isEnum()) {
            String value = json.getValue();
            return ClassCastUtil.toEnum(value, typeClass);
        } else {
            throw new JsonMapperException("Can not map " + JsonString.class.getSimpleName() + " to type " + typeInfo.getType().getTypeName());
        }
    }

    /**
     * Maps an {@link Enum} to a {@link JsonString}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped string json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonString mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();
        if (!clazz.isEnum()) {
            throw new JsonMapperException("Can not map " + clazz.getTypeName() + " to " + JsonString.class.getSimpleName());
        }

        Enum<?> value = (Enum<?>) object;
        return new JsonString(value.name());
    }
}
