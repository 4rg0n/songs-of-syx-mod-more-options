package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * For handling {@link Class} related things :)
 */
@UtilityClass
public class ClassUtil {

    /**
     * Checks whether the given object is an instance of the given class.
     *
     * @return whether the given object is an instance of the given class
     */
    public static boolean instanceOf(Object object, Class<?> clazz) {
        return instanceOf(object.getClass(), clazz);
    }

    /**
     * Checks whether the given object is an instance of the other object.
     *
     * @return whether the given object is an instance of the other object
     */
    public static boolean instanceOf(Object object, Object otherObject) {
        return instanceOf(object.getClass(), otherObject.getClass());
    }

    /**
     * Checks whether the given class is an instance of the other class.
     *
     * @return whether the given class is an instance of the other class
     */
    public static boolean instanceOf(@Nullable Class<?> clazz, @Nullable Class<?> otherClazz) {
        if (clazz == null && otherClazz == null) {
            return true;
        }

        if (clazz == null || otherClazz == null) {
            return false;
        }

        if (clazz.getCanonicalName().equals(otherClazz.getCanonicalName())) {
            return true;
        }

        if (otherClazz.isAssignableFrom(clazz)) {
            return true;
        }

        return false;
    }

    /**
     * Checks whether the given classes are canonically the same.
     *
     * @return whether the given classes are canonically the same
     */
    public static boolean sameAs(Class<?> clazz1, Class<?> clazz2) {
        return clazz1.getCanonicalName().equals(clazz2.getCanonicalName());
    }
}
