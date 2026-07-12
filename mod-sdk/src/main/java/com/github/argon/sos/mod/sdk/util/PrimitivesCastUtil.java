package com.github.argon.sos.mod.sdk.util;


import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/**
 * For handling the casting objects into primitive data types.
 */
@UtilityClass
public class PrimitivesCastUtil {

    /**
     * Casts a primitive long array to a primitive integer array.
     *
     * @param longs to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = toInteger(longs[i]);
        }

        return ints;
    }

    /**
     * Casts a {@link Long} array to a primitive integer array.
     *
     * @param longs to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(Long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = toInteger(longs[i]);
        }

        return ints;
    }

    /**
     * Casts a {@link Long} {@link Collection} to a primitive integer array.
     *
     * @param longs to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArrayLong(Collection<Long> longs) {
        int[] ints = new int[longs.size()];
        int i = 0;

        for (Long aLong : longs) {
            ints[i] = toInteger(aLong);
            i++;
        }

        return ints;
    }

    /**
     * Casts a primitive byte array to a primitive integer array.
     *
     * @param bytes to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    /**
     * Casts a {@link Byte} array to a primitive integer array.
     *
     * @param bytes to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(Byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    /**
     * Casts a {@link Byte} {@link Collection} to a primitive integer array.
     *
     * @param bytes to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArrayByte(Collection<Byte> bytes) {
        int[] ints = new int[bytes.size()];
        int i = 0;

        for (Byte aByte : bytes) {
            ints[i] = aByte;
            i++;
        }

        return ints;
    }

    /**
     * Casts a primitive shorts array to a primitive integer array.
     *
     * @param shorts to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(short[] shorts) {
        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    /**
     * Casts a primitive integer array to a primitive long array.
     *
     * @param integers to cast
     * @return cast primitive long array
     */
    public static long[] toLongArray(int[] integers) {
        long[] longs = new long[integers.length];

        for (int i = 0, length = integers.length; i < length; i++) {
            longs[i] = integers[i];
        }

        return longs;
    }

    /**
     * Casts a primitive integer array to a primitive byte array.
     *
     * @param integers to cast
     * @return cast primitive byte array
     */
    public static byte[] toByteArray(int[] integers) {
        byte[] bytes = new byte[integers.length];

        for (int i = 0, length = integers.length; i < length; i++) {
            bytes[i] = ClassCastUtil.toByte(integers[i]);
        }

        return bytes;
    }

    /**
     * Casts a primitive integer array to a primitive short array.
     *
     * @param integers to cast
     * @return cast primitive short array
     */
    public static short[] toShortArray(int[] integers) {
        short[] shorts = new short[integers.length];

        for (int i = 0, length = integers.length; i < length; i++) {
            shorts[i] = ClassCastUtil.toShort(integers[i]);
        }

        return shorts;
    }

    /**
     * Casts a primitive double array to a primitive float array.
     *
     * @param doubles to cast
     * @return cast primitive float array
     */
    public static float[] toFloatArray(double[] doubles) {
        float[] floats = new float[doubles.length];

        for (int i = 0, length = doubles.length; i < length; i++) {
            floats[i] = ClassCastUtil.toFloat(doubles[i]);
        }

        return floats;
    }

    /**
     * Casts a {@link Short} array to a primitive integer array.
     *
     * @param shorts to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(Short[] shorts) {

        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    /**
     * Casts a {@link Short} {@link Collection} to a primitive integer array.
     *
     * @param shorts to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArrayShort(Collection<Short> shorts) {
        int[] ints = new int[shorts.size()];
        int i = 0;

        for (Short aShort : shorts) {
            ints[i] = aShort;
            i++;
        }

        return ints;
    }

    /**
     * Casts an {@link Integer} array to a primitive integer array.
     *
     * @param integers to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArray(Integer[] integers) {
        return Arrays.stream(integers)
            .mapToInt(Integer::intValue)
            .toArray();
    }

    /**
     * Casts an {@link Integer} {@link Collection} to a primitive integer array.
     *
     * @param integers to cast
     * @return cast primitive integer array
     */
    public static int[] toIntegerArrayInteger(Collection<Integer> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;

        for (Integer integer : integers) {
            ints[i] = integer;
            i++;
        }

        return ints;
    }

    /**
     * Casts a primitive float array to a primitive double array.
     *
     * @param floats to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArray(float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = floats[i];
        }

        return doubles;
    }

    /**
     * Casts a {@link Float} array to a primitive double array.
     *
     * @param floats to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArray(Float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = toDouble(floats[i]);
        }

        return doubles;
    }

    /**
     * Casts a {@link BigDecimal} array to a primitive double array.
     *
     * @param bigDecimals to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArray(BigDecimal[] bigDecimals) {
        double[] doubles = new double[bigDecimals.length];

        for (int i = 0, length = bigDecimals.length; i < length; i++) {
            doubles[i] = toDouble(bigDecimals[i]);
        }

        return doubles;
    }

    /**
     * Casts a {@link BigDecimal} {@link Collection} to a primitive double array.
     *
     * @param bigDecimals to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArrayBigDecimal(Collection<BigDecimal> bigDecimals) {
        double[] doubles = new double[bigDecimals.size()];
        int i = 0;

        for (BigDecimal bigDecimal : bigDecimals) {
            doubles[i] = toDouble(bigDecimal);
            i++;
        }

        return doubles;
    }

    /**
     * Casts a {@link Float} {@link Collection} to a primitive double array.
     *
     * @param floats to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArrayFloat(Collection<Float> floats) {
        double[] doubles = new double[floats.size()];
        int i = 0;

        for (Float aFloat : floats) {
            doubles[i] = aFloat;
            i++;
        }

        return doubles;
    }

    /**
     * Casts a {@link Double} array to a primitive double array.
     *
     * @param doubles to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArray(Double[] doubles) {
        return Arrays.stream(doubles)
            .mapToDouble(Double::doubleValue)
            .toArray();
    }

    /**
     * Casts a {@link Float} {@link Collection} to a primitive double array.
     *
     * @param doubles to cast
     * @return cast primitive double array
     */
    public static double[] toDoubleArrayDouble(Collection<Double> doubles) {
        double[] doublesCasted = new double[doubles.size()];
        int i = 0;

        for (Double aDouble : doubles) {
            doublesCasted[i] = aDouble;
            i++;
        }

        return doublesCasted;
    }

    /**
     * Casts a {@link Float} to a primitive double.
     *
     * @param aFloat to cast
     * @return primitive double
     */
    public static double toDouble(Float aFloat) {
        return aFloat.doubleValue();
    }

    /**
     * Casts a {@link BigDecimal} to a primitive double.
     *
     * @param bigDecimal to cast
     * @return primitive double
     */
    public static double toDouble(BigDecimal bigDecimal) {
        return bigDecimal.doubleValue();
    }

    /**
     * Casts a {@link Short} to a primitive integer.
     *
     * @param aShort to cast
     * @return primitive integer
     */
    public static int toInteger(Short aShort) {
        return aShort.intValue();
    }

    /**
     * Casts a {@link Integer} to a primitive integer.
     *
     * @param integer to cast
     * @return primitive integer
     */
    public static int toInteger(Integer integer) {
        return integer;
    }

    /**
     * Casts a {@link Long} to a primitive integer.
     *
     * @param aLong to cast
     * @return primitive integer
     */
    public static int toInteger(Long aLong) {
        return aLong.intValue();
    }

    /**
     * Casts a {@link Byte} to a primitive integer.
     *
     * @param aByte to cast
     * @return primitive integer
     */
    public static int toInteger(Byte aByte) {
        return aByte.intValue();
    }
}
