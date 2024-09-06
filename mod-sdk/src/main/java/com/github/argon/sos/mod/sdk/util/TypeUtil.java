package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeUtil {

    /**
     * Returns the parent type when the type is a generic
     * E.g. when the type is the String from a List<String>, the raw type would be the List
     *
     * @param type to get the parent type from
     * @return raw type
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        } else {
            return (Class<?>) type;
        }
    }

    /**
     * @return whether a type is assignable to a given class
     */
    public static boolean isAssignableFrom(Type type, Class<?> clazz) {
        return clazz.isAssignableFrom(getRawType(type));
    }

    /**
     * @return whether a type is the same as a given class
     */
    public static boolean sameAs(Type type, Class<?> clazz) {
        return type.getTypeName().equals(clazz.getTypeName());
    }
}
