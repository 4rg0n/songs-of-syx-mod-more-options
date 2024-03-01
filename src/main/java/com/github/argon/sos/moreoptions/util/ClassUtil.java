package com.github.argon.sos.moreoptions.util;

public class ClassUtil {
    public static boolean instanceOf(Object object, Class<?> clazz) {
        return instanceOf(object.getClass(), clazz);
    }

    public static boolean instanceOf(Object object, Object otherObject) {
        return instanceOf(object.getClass(), otherObject.getClass());
    }

    /**
     * @param clazz nullable
     * @param otherClazz nullable
     */
    public static boolean instanceOf(Class<?> clazz, Class<?> otherClazz) {
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

    public static boolean sameAs(Class<?> clazz1, Class<?> clazz2) {
        return clazz1.getCanonicalName().equals(clazz2.getCanonicalName());
    }
}
