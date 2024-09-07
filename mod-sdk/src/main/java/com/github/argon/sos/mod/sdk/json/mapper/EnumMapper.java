package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonString;

import static com.github.argon.sos.mod.sdk.util.ClassCastUtil.toEnum;

public class EnumMapper implements Mapper<JsonString> {
    @Override
    public boolean supports(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return clazz.isEnum();
    }

    @Override
    public Object mapJson(JsonString json, TypeInfo<?> typeInfo) {
        Class<?> typeClass = typeInfo.getTypeClass();

        if (typeClass.isEnum()) {
            String value = json.getValue();
            return toEnum(value, typeClass);
        } else {
            throw new JsonMapperException("Can not map " + JsonString.class.getSimpleName() + " to type " + typeInfo.getType().getTypeName());
        }
    }

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
