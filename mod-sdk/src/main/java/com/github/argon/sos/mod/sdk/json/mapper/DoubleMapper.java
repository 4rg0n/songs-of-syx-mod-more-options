package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonDouble;
import com.github.argon.sos.mod.sdk.util.ClassCastUtil;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.TypeUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * For mapping {@link JsonDouble} to a java {@link Double} and visa versa.
 */
public class DoubleMapper implements Mapper<JsonDouble> {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Arrays.asList(Double.class, Float.class, BigDecimal.class, double.class, float.class)
            .contains(clazz);
    }

    /**
     * Maps a {@link JsonDouble} to a {@link Double}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonDouble json, TypeInfo<?> typeInfo) {
        Double value = json.getValue();
        Type type = typeInfo.getType();

        if (TypeUtil.isAssignableFrom(type, Double.class) || TypeUtil.isAssignableFrom(type, double.class)) {
            return value;
        } else if (TypeUtil.isAssignableFrom(type, Float.class) || TypeUtil.isAssignableFrom(type, float.class)) {
            return ClassCastUtil.toFloat(value);
        }  else if (TypeUtil.isAssignableFrom(type, BigDecimal.class)) {
            return ClassCastUtil.toBigDecimal(value);
        } else {
            throw new JsonMapperException("Can not map " + JsonDouble.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    /**
     * Maps a {@link Double} to a {@link JsonDouble}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped double json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonDouble mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Double) {
            return new JsonDouble((Double) object);
        } else if (ClassUtil.sameAs(clazz, double.class)) {
            return new JsonDouble((double) object);
        } else if (object instanceof Float value) {
            return new JsonDouble(value.doubleValue());
        } else if (ClassUtil.sameAs(clazz, float.class)) {
            float value = (float) object;
            return new JsonDouble((double) value);
        } else if (object instanceof BigDecimal value) {
            return new JsonDouble(value.doubleValue());
        } else {
            throw new JsonMapperException("Can not map " + clazz.getTypeName() + " to " + JsonDouble.class.getSimpleName());
        }
    }
}
