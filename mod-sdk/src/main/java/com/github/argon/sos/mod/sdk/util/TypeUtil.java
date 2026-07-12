package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Utility class for dealing with {@link Type}s.
 */
@UtilityClass
public class TypeUtil {

    /**
     * Returns the parent type when the type is a generic
     * E.g. when the type is the {@code String} from a {@code List<String>}, the raw type would be the {@code List}
     *
     * @param type to get the parent type from
     * @return raw type
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            return (Class<?>) rawType;
        } else {
            return (Class<?>) type;
        }
    }

    /**
     * Checks whether a given class is assignable from a given {@link Type}.
     *
     * @param type to check whether it is assignable
     * @param clazz to check the type against
     * @return whether a type is assignable to a given class
     */
    public static boolean isAssignableFrom(Type type, Class<?> clazz) {
        return clazz.isAssignableFrom(getRawType(type));
    }

    /**
     * Checks whether a given {@link Type} is equal to a given class.
     *
     * @param type to check
     * @param clazz to check the type against
     * @return whether a type is the same as a given class
     */
    public static boolean sameAs(Type type, Class<?> clazz) {
        return type.getTypeName().equals(clazz.getTypeName());
    }
}
