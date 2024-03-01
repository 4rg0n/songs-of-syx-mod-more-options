package com.github.argon.sos.moreoptions.json.mapper;

import com.github.argon.sos.moreoptions.json.element.JsonDouble;
import com.github.argon.sos.moreoptions.util.ClassCastUtil;
import com.github.argon.sos.moreoptions.util.ClassUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.github.argon.sos.moreoptions.util.TypeUtil.isAssignableFrom;

public class DoubleMapper implements Mapper<JsonDouble> {
    @Override
    public boolean supports(Class<?> clazz) {
        return Arrays.asList(Double.class, Float.class, BigDecimal.class, double.class, float.class)
            .contains(clazz);
    }

    @Override
    public Object mapJson(JsonDouble json, TypeInfo<?> typeInfo) {
        Double value = json.getValue();
        Type type = typeInfo.getType();

        if (isAssignableFrom(type, Double.class) || isAssignableFrom(type, double.class)) {
            return value;
        } else if (isAssignableFrom(type, Float.class) || isAssignableFrom(type, float.class)) {
            return ClassCastUtil.toFloat(value);
        }  else if (isAssignableFrom(type, BigDecimal.class)) {
            return ClassCastUtil.toBigDecimal(value);
        } else {
            throw new JsonMapperException("Can not map " + JsonDouble.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    @Override
    public JsonDouble mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = object.getClass();

        if (object instanceof Double) {
            return new JsonDouble((Double) object);
        } else if (ClassUtil.sameAs(clazz, double.class)) {
            return new JsonDouble((double) object);
        } else if (object instanceof Float) {
            Float value = (Float) object;
            return new JsonDouble(value.doubleValue());
        } else if (ClassUtil.sameAs(clazz, float.class)) {
            Float value = Float.valueOf((float) object);
            return new JsonDouble(value.doubleValue());
        } else if (object instanceof BigDecimal) {
            BigDecimal value = (BigDecimal) object;
            return new JsonDouble(value.doubleValue());
        } else {
            throw new JsonMapperException("Can not map " + clazz.getTypeName() + " to " + JsonDouble.class.getSimpleName());
        }
    }
}
