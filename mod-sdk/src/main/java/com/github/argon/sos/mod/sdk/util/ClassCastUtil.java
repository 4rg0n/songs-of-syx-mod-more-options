package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;

/**
 * For transforming all kind of classes into different classes.
 * Mostly for transforming single numbers and arrays of numbers.
 */
@UtilityClass
public class ClassCastUtil {
    /**
     * Will transform a primitive int to a {@link Long}.
     *
     * @param integer to transform
     * @return long object
     */
    public static Long toLong(int integer) {
        return (long) integer;
    }

    /**
     * Will transform a primitive int to a {@link Byte}.
     *
     * @param integer to transform
     * @return byte object
     */
    public static Byte toByte(int integer) {
        if (integer >= Byte.MAX_VALUE || integer <= Byte.MIN_VALUE) {
            throw new IllegalArgumentException("Integer value " + integer + " must be between " + Byte.MIN_VALUE + " and " + Byte.MAX_VALUE + ".");
        }

        return (byte) integer;
    }

    /**
     * Will transform a primitive int to a {@link Short}.
     *
     * @param integer to transform
     * @return long object
     */
    public static Short toShort(int integer) {
        if (integer >= Short.MAX_VALUE || integer <= Short.MIN_VALUE) {
            throw new IllegalArgumentException("Integer value " + integer + " must be between " + Short.MIN_VALUE + " and " + Short.MAX_VALUE + ".");
        }

        return (short) integer;
    }

    /**
     * Will transform a primitive double to a {@link Float}.
     *
     * @param aDouble to transform
     * @return float object
     */
    public static Float toFloat(double aDouble) {
        if (aDouble >= Float.MAX_VALUE || aDouble <= Float.MIN_VALUE) {
            throw new IllegalArgumentException("Double value " + aDouble + " must be between " + Float.MIN_VALUE + " and " + Float.MAX_VALUE + ".");
        }

        return (float) aDouble;
    }

    /**
     * Will transform a primitive double to a {@link BigDecimal}.
     *
     * @param aDouble to transform
     * @return big decimal object
     */
    public static BigDecimal toBigDecimal(double aDouble) {
        return BigDecimal.valueOf(aDouble);
    }

    /**
     * Tries to transform the given {@link String} to the given {@link Enum} class.
     *
     * @param string value for the enum
     * @param clazz of the enum
     * @return casted enum
     * @throws ClassCastException if transformation fails
     */
    public static Enum<?> toEnum(String string, Class<?> clazz) {
        //noinspection unchecked,rawtypes
        return Enum.valueOf((Class<Enum>) clazz, string);
    }

    /**
     * Will try to transform an {@link Object[]} to a {@link String[]}.
     *
     * @param objects to transform
     * @return string array
     */
    public static String[] toStringArray(Object[] objects) {
        String[] strings = new String[objects.length];

        for (int i = objects.length - 1; i >= 0; i--) {
            Object object = objects[i];
            strings[i] = toString(object);
        }

        return strings;
    }

    /**
     * Will transform a {@link Collection} of {@link String}s to a {@link String[]}.
     *
     * @param strings to transform
     * @return string array
     */
    public static String[] toStringArrayString(Collection<?> strings) {
        //noinspection SuspiciousToArrayCall
        return strings.toArray(
            strings.toArray(new String[0])
        );
    }

    /**
     * Will try to transform a {@link Collection} of {@link Enum}s to a {@link String[]}.
     *
     * @param enums to transform
     * @return string array
     */
    public static String[] toStringArrayEnum(Collection<?> enums) {
        return enums.stream()
            .map(object -> toString((Enum<?>) object))
            .toArray(String[]::new);
    }

    /**
     * Will transform an {@link Enum} to a {@link String} via {@link Enum#name()}.
     *
     * @param aEnum to transform
     * @return name of the enum
     */
    public static String toString(Enum<?> aEnum) {
        return aEnum.name();
    }

    /**
     * Will try to transform any {@link Object} to a {@link String}.
     *
     * @param object to transform
     * @return string
     */
    public static String toString(Object object) {
        if (object instanceof Enum<?>) {
            return toString((Enum<?>) object);
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Temporal temporal) {
            return temporal.toString();
        } else {
            return object.toString();
        }
    }

    /**
     * Will try to transform a {@link String[]} to a {@link Enum[]} with the given class.
     *
     * @param strings to transform
     * @param clazz of the enum
     * @return enum array
     */
    public static Enum<?>[] toEnumArray(String[] strings, Class<?> clazz) {
        Enum<?>[] enums = (Enum<?>[]) Array.newInstance(clazz, strings.length);

        for (int i = 0, length = strings.length; i < length; i++) {
            enums[i] = toEnum(strings[i], clazz);
        }

        return enums;
    }

    /**
     * Will transform a primitive double array to a {@link Double[]}.
     *
     * @param primitiveDoubles to transform
     * @return double object array
     */
    public static Double[] toDoubleArray(double[] primitiveDoubles) {
        Double[] doubles = new Double[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            doubles[i] = primitiveDoubles[i];
        }

        return doubles;
    }

    /**
     * Will transform a primitive double array to a {@link Float[]}.
     *
     * @param primitiveDoubles to transform
     * @return float object array
     */
    public static Float[] toFloatArray(double[] primitiveDoubles) {
        Float[] floats = new Float[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            floats[i] = toFloat(primitiveDoubles[i]);
        }

        return floats;
    }

    /**
     * Will transform a primitive int array to a {@link Integer[]}.
     *
     * @param primitiveIntegers to transform
     * @return integer object array
     */
    public static Integer[] toIntegerArray(int[] primitiveIntegers) {
        Integer[] integers = new Integer[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            integers[i] = primitiveIntegers[i];
        }

        return integers;
    }

    /**
     * Will transform a primitive int array to a {@link Long[]}.
     *
     * @param primitiveIntegers to transform
     * @return long object array
     */
    public static Long[] toLongArray(int[] primitiveIntegers) {
        Long[] longs = new Long[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            longs[i] = toLong(primitiveIntegers[i]);
        }

        return longs;
    }

    /**
     * Will transform a primitive int array to a {@link Byte[]}.
     *
     * @param primitiveIntegers to transform
     * @return byte object array
     */
    public static Byte[] toByteArray(int[] primitiveIntegers) {
        Byte[] bytes = new Byte[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            bytes[i] = toByte(primitiveIntegers[i]);
        }

        return bytes;
    }

    /**
     * Will transform a primitive int array to a {@link Short[]}.
     *
     * @param primitiveIntegers to transform
     * @return short object array
     */
    public static Short[] toShortArray(int[] primitiveIntegers) {
        Short[] shorts = new Short[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            shorts[i] = toShort(primitiveIntegers[i]);
        }

        return shorts;
    }

    /**
     * Will transform a primitive double array to a {@link BigDecimal[]}.
     *
     * @param primitiveDoubles to transform
     * @return big decimal object array
     */
    public static BigDecimal[] toBigDecimalArray(double[] primitiveDoubles) {
        BigDecimal[] bigDecimals = new BigDecimal[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            bigDecimals[i] = toBigDecimal(primitiveDoubles[i]);
        }

        return bigDecimals;
    }

    /**
     * Checks whether given object is an array.
     *
     * @param object to check
     * @return whether given object is an array
     */
    public static boolean isArray(Object object) {
        return object.getClass().isArray();
    }

    /**
     * Transforms an array of any objects to a {@link Collection}.
     *
     * @param objects to transform
     * @return collection with objects
     * @param <T> type of the objects to transform
     */
    public static <T> Collection<T> toCollection(T[] objects) {
       return Arrays.stream(objects)
           .toList();
    }
}
