package com.github.argon.sos.moreoptions.json.mapper;

import com.github.argon.sos.moreoptions.json.element.JsonLong;
import com.github.argon.sos.mod.sdk.util.ClassCastUtil;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.TypeUtil;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Arrays;

import static com.github.argon.sos.mod.sdk.util.TypeUtil.isAssignableFrom;

public class LongMapper implements Mapper<JsonLong> {
    @Override
    public boolean supports(Class<?> clazz) {
        return Arrays.asList(
            Integer.class,
            Byte.class,
            Short.class,
            Long.class,
            BigInteger.class,
            int.class,
            short.class,
            byte.class,
            long.class
        ).contains(clazz);
    }

    @Override
    public Object mapJson(JsonLong json, TypeInfo<?> typeInfo) {
        Long value = json.getValue();
        Type type = typeInfo.getType();

        if (isAssignableFrom(type, Long.class) || TypeUtil.sameAs(type, long.class)) {
            return value;
        } else if (isAssignableFrom(type, Integer.class) || TypeUtil.sameAs(type, int.class)) {
            return value.intValue();
        } else if (isAssignableFrom(type, Byte.class) || TypeUtil.sameAs(type, byte.class)) {
            return ClassCastUtil.toByte(value.intValue());
        } else if (isAssignableFrom(type, Short.class) || TypeUtil.sameAs(type, short.class)) {
            return value.shortValue();
        } else if (isAssignableFrom(type, BigInteger.class) ) {
            return BigInteger.valueOf(value);
        } else {
            throw new JsonMapperException("Can not map " + JsonLong.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    @Override
    public JsonLong mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Long) {
            return new JsonLong((Long) object);
        } else if (ClassUtil.sameAs(clazz, long.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Integer) {
            Integer value = (Integer) object;
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, int.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Byte) {
            Byte value = (Byte) object;
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, byte.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Short) {
            Short value = (Short) object;
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, short.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof BigInteger) {
            BigInteger value = (BigInteger) object;
            return new JsonLong(value.longValue());
        } else {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " + JsonLong.class.getSimpleName());
        }
    }
}
