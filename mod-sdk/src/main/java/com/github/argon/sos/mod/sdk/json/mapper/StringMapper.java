package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonString;

import static com.github.argon.sos.mod.sdk.util.ClassUtil.instanceOf;

public class StringMapper implements Mapper<JsonString> {

    @Override
    public boolean supports(Class<?> clazz) {
        return instanceOf(clazz, CharSequence.class);
    }

    @Override
    public Object mapJson(JsonString json, TypeInfo<?> typeInfo) {
        Class<?> typeClass = typeInfo.getTypeClass();

        if (instanceOf(typeClass, CharSequence.class)) {
            return json.getValue();
        } else {
            throw new JsonMapperException("Can not map " + JsonString.class.getSimpleName() + " to type " + typeInfo.getType().getTypeName());
        }
    }

    @Override
    public JsonString mapObject(Object object, TypeInfo<?> typeInfo) {
        if (object instanceof CharSequence) {
            return new JsonString((String) object);
        } else {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " +  JsonString.class.getSimpleName());
        }
    }
}
