package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonBoolean;

import java.util.Arrays;

import static com.github.argon.sos.mod.sdk.util.ClassUtil.sameAs;

public class BooleanMapper implements Mapper<JsonBoolean> {
    @Override
    public boolean supports(Class<?> clazz) {
        return Arrays.asList(Boolean.class, boolean.class)
            .contains(clazz);
    }

    @Override
    public Object mapJson(JsonBoolean json, TypeInfo<?> typeInfo) {
        return json.getValue();
    }

    @Override
    public JsonBoolean mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Boolean) {
            Boolean value = (Boolean) object;
            return new JsonBoolean(value);
        } else if (sameAs(clazz, boolean.class)) {
            boolean value = (boolean) object;
            return new JsonBoolean(value);
        } else {
            throw new JsonMapperException("Can not map " + clazz.getTypeName() + " to " + JsonBoolean.class.getSimpleName());
        }
    }
}
