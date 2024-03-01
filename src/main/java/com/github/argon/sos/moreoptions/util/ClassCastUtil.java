package com.github.argon.sos.moreoptions.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ClassCastUtil {
    public static Long toLong(int integer) {
        return (long) integer;
    }

    public static Byte toByte(int integer) {
        return (byte) integer;
    }

    public static Short toShort(int integer) {
        return (short) integer;
    }

    public static Float toFloat(double aDouble) {
        return (float) aDouble;
    }

    public static BigDecimal toBigDecimal(double aDouble) {
        return BigDecimal.valueOf(aDouble);
    }

    public static Enum<?> toEnum(String string, Class<?> clazz) {
        //noinspection unchecked,rawtypes
        return Enum.valueOf((Class<Enum>) clazz, string);
    }

    public static String[] toStringArray(Object[] objects) {
        String[] strings = new String[objects.length];

        for (int i = objects.length - 1; i >= 0; i--) {
            Object object = objects[i];
            strings[i] = toString(object);
        }

        return strings;
    }

    public static String[] toStringArrayString(Collection<?> strings) {
        //noinspection SuspiciousToArrayCall
        return strings.toArray(
            strings.toArray(new String[0])
        );
    }

    public static String[] toStringArrayEnum(Collection<?> enums) {
        return enums.stream()
            .map(object -> toString((Enum<?>) object))
            .toArray(String[]::new);
    }

    public static String toString(Enum<?> aEnum) {
        return aEnum.name();
    }

    public static String toString(Object object) {
        if (object instanceof Enum<?>) {
            return toString((Enum<?>) object);
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Temporal) {
            Temporal temporal = (Temporal) object;
            return temporal.toString();
        } else {
            return object.toString();
        }
    }

    public static Enum<?>[] toEnumArray(String[] strings, Class<?> clazz) {
        Enum<?>[] enums = (Enum<?>[]) Array.newInstance(clazz, strings.length);

        for (int i = 0, length = strings.length; i < length; i++) {
            enums[i] = toEnum(strings[i], clazz);
        }

        return enums;
    }

    public static Double[] toDoubleArray(double[] primitiveDoubles) {
        Double[] doubles = new Double[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            doubles[i] = primitiveDoubles[i];
        }

        return doubles;
    }

    public static Float[] toFloatArray(double[] primitiveDoubles) {
        Float[] floats = new Float[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            floats[i] = toFloat(primitiveDoubles[i]);
        }

        return floats;
    }

    public static Integer[] toIntegerArray(int[] primitiveIntegers) {
        Integer[] integers = new Integer[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            integers[i] = primitiveIntegers[i];
        }

        return integers;
    }

    public static Long[] toLongArray(int[] primitiveIntegers) {
        Long[] longs = new Long[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            longs[i] = toLong(primitiveIntegers[i]);
        }

        return longs;
    }

    public static Byte[] toByteArray(int[] primitiveIntegers) {
        Byte[] bytes = new Byte[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            bytes[i] = toByte(primitiveIntegers[i]);
        }

        return bytes;
    }

    public static Short[] toShortArray(int[] primitiveIntegers) {
        Short[] shorts = new Short[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            shorts[i] = toShort(primitiveIntegers[i]);
        }

        return shorts;
    }

    public static BigDecimal[] toBigDecimalArray(double[] primitiveDoubles) {
        BigDecimal[] bigDecimals = new BigDecimal[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            bigDecimals[i] = toBigDecimal(primitiveDoubles[i]);
        }

        return bigDecimals;
    }

    public static boolean isArray(Object object) {
        return object.getClass().isArray();
    }

    public static <T> Collection<T> toCollection(T[] objects) {
       return Arrays.stream(objects)
           .collect(Collectors.toList());
    }
}
