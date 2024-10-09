package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * For handling {@link Class} related things :)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtil {

    /**
     * @return whether the given object is an instance of the given class
     */
    public static boolean instanceOf(Object object, Class<?> clazz) {
        return instanceOf(object.getClass(), clazz);
    }

    /**
     * @return whether the given object is an instance of the other object
     */
    public static boolean instanceOf(Object object, Object otherObject) {
        return instanceOf(object.getClass(), otherObject.getClass());
    }

    /**
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
     * @return whether the given classes are canonically the same
     */
    public static boolean sameAs(Class<?> clazz1, Class<?> clazz2) {
        return clazz1.getCanonicalName().equals(clazz2.getCanonicalName());
    }

    /**
     * @return e.g. "Map.Entry" for the {@link java.util.Map.Entry} class
     */
    public static String getInnerName(Class<?> clazz) {
        String fullName = clazz.getCanonicalName();

       return Arrays.stream(fullName.split("\\."))
            .filter(part -> Character.isUpperCase(part.charAt(0)))
            .collect(Collectors.joining("."));
    }

    /**
     * Will replace the packages with a single character in a class name.
     * E.g. "java.lang.String" will be transformed to "j.l.String"
     *
     * @param clazz to read the class name from
     * @return shortened class name
     */
    public static String shortenClassName(Class<?> clazz) {
        return shortenPackageName(clazz.getPackage().getName()) + '.' + clazz.getSimpleName();
    }

    /**
     * Will replace the packages with a single character in a package name.
     * E.g. "java.lang" will be transformed to "j.l"
     *
     * @param packageName to shorten
     * @return shortened class name
     */
    public static String shortenPackageName(String packageName) {
        if (packageName.isEmpty()) {
            return packageName;
        }

        return Arrays.stream(packageName.split("\\."))
            .filter(string -> !string.isEmpty())
            .map(segment -> Character.toString(segment.charAt(0)))
            .collect(Collectors.joining("."));
    }
}
