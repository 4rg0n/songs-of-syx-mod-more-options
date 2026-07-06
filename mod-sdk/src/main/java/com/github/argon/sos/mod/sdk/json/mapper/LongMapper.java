package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonLong;
import com.github.argon.sos.mod.sdk.util.ClassCastUtil;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.TypeUtil;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * For mapping {@link JsonLong} to a java {@link Long}, {@link Integer}, {@link Byte}, {@link Short} or {@link BigInteger} and visa versa.
 */
public class LongMapper implements Mapper<JsonLong> {
    /**
     * {@inheritDoc}
     */
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

    /**
     * Maps a {@link JsonLong} to a {@link Long}, {@link Integer}, {@link Byte}, {@link Short} or {@link BigInteger}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonLong json, TypeInfo<?> typeInfo) {
        Long value = json.getValue();
        Type type = typeInfo.getType();

        if (TypeUtil.isAssignableFrom(type, Long.class) || TypeUtil.sameAs(type, long.class)) {
            return value;
        } else if (TypeUtil.isAssignableFrom(type, Integer.class) || TypeUtil.sameAs(type, int.class)) {
            return value.intValue();
        } else if (TypeUtil.isAssignableFrom(type, Byte.class) || TypeUtil.sameAs(type, byte.class)) {
            return ClassCastUtil.toByte(value.intValue());
        } else if (TypeUtil.isAssignableFrom(type, Short.class) || TypeUtil.sameAs(type, short.class)) {
            return value.shortValue();
        } else if (TypeUtil.isAssignableFrom(type, BigInteger.class) ) {
            return BigInteger.valueOf(value);
        } else {
            throw new JsonMapperException("Can not map " + JsonLong.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    /**
     * Maps a {@link Long}, {@link Integer}, {@link Byte}, {@link Short} to a {@link JsonLong}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped boolean json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonLong mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Long) {
            return new JsonLong((Long) object);
        } else if (ClassUtil.sameAs(clazz, long.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Integer value) {
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, int.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Byte value) {
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, byte.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof Short value) {
            return new JsonLong(value.longValue());
        } else if (ClassUtil.sameAs(clazz, short.class)) {
            return new JsonLong((long) object);
        } else if (object instanceof BigInteger value) {
            return new JsonLong(value.longValue());
        } else {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " + JsonLong.class.getSimpleName());
        }
    }
}
