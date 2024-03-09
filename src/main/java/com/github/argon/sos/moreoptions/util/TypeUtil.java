package com.github.argon.sos.moreoptions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeUtil {
    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        } else {
            return (Class<?>) type;
        }
    }

    public static boolean isAssignableFrom(Type type, Class<?> clazz) {
        return clazz.isAssignableFrom(getRawType(type));
    }

    public static boolean sameAs(Type type, Class<?> clazz) {
        return type.getTypeName().equals(clazz.getTypeName());
    }
}
