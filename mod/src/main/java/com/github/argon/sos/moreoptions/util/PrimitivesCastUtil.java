package com.github.argon.sos.moreoptions.util;


import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitivesCastUtil {

    private final static Logger log = Loggers.getLogger(PrimitivesCastUtil.class);

    public static int[] toIntegerArray(long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = toInteger(longs[i]);
        }

        return ints;
    }

    public static int[] toIntegerArray(Long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = toInteger(longs[i]);
        }

        return ints;
    }

    public static int[] toIntegerArrayLong(Collection<?> longs) {
        int[] ints = new int[longs.size()];
        int i = 0;

        for (Object object : longs) {
            ints[i] = toInteger((Long) object);
            i++;
        }

        return ints;
    }

    public static int[] toIntegerArray(byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    public static int[] toIntegerArray(Byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    public static int[] toIntegerArrayByte(Collection<?> bytes) {
        int[] ints = new int[bytes.size()];
        int i = 0;

        for (Object object : bytes) {
            Byte aByte = (Byte) object;
            ints[i] = aByte;
            i++;
        }

        return ints;
    }

    public static int[] toIntegerArray(short[] shorts) {
        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    public static long[] toLongArray(int[] inetgers) {
        long[] longs = new long[inetgers.length];

        for (int i = 0, length = inetgers.length; i < length; i++) {
            longs[i] = inetgers[i];
        }

        return longs;
    }

    public static byte[] toByteArray(int[] primitiveIntegers) {
        byte[] bytes = new byte[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            bytes[i] = ClassCastUtil.toByte(primitiveIntegers[i]);
        }

        return bytes;
    }

    public static short[] toShortArray(int[] primitiveIntegers) {
        short[] shorts = new short[primitiveIntegers.length];

        for (int i = 0, length = primitiveIntegers.length; i < length; i++) {
            shorts[i] = ClassCastUtil.toShort(primitiveIntegers[i]);
        }

        return shorts;
    }

    public static float[] toFloatArray(double[] primitiveDoubles) {
        float[] floats = new float[primitiveDoubles.length];

        for (int i = 0, length = primitiveDoubles.length; i < length; i++) {
            floats[i] = ClassCastUtil.toFloat(primitiveDoubles[i]);
        }

        return floats;
    }

    public static int[] toIntegerArray(Short[] shorts) {

        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    public static int[] toIntegerArrayShort(Collection<?> shorts) {
        int[] ints = new int[shorts.size()];
        int i = 0;

        for (Object object : shorts) {
            Short aShort = (Short) object;
            ints[i] = aShort;
            i++;
        }

        return ints;
    }

    public static int[] toIntegerArray(Integer[] integers) {
        return Arrays.stream(integers)
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public static int[] toIntegerArrayInteger(Collection<?> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;

        for (Object object : integers) {
            Integer integer = (Integer) object;
            ints[i] = integer;
            i++;
        }

        return ints;
    }

    public static double[] toDoubleArray(float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = floats[i];
        }

        return doubles;
    }

    public static double[] toDoubleArray(Float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = toDouble(floats[i]);
        }

        return doubles;
    }

    public static double[] toDoubleArray(BigDecimal[] bigDecimals) {
        double[] doubles = new double[bigDecimals.length];

        for (int i = 0, length = bigDecimals.length; i < length; i++) {
            doubles[i] = toDouble(bigDecimals[i]);
        }

        return doubles;
    }

    public static double[] toeDoubleArrayBigDecimal(Collection<?> bigDecimals) {
        double[] doubles = new double[bigDecimals.size()];
        int i = 0;

        for (Object object : bigDecimals) {
            doubles[i] = toDouble((BigDecimal) object);
            i++;
        }

        return doubles;
    }

    public static double[] toDoubleArrayFloat(Collection<?> floats) {
        double[] doubles = new double[floats.size()];
        int i = 0;

        for (Object object : floats) {
            Float aFloat = (Float) object;
            doubles[i] = aFloat;
            i++;
        }

        return doubles;
    }

    public static double[] toDoubleArray(Double[] doubles) {
        return Arrays.stream(doubles)
            .mapToDouble(Double::doubleValue)
            .toArray();
    }

    public static double[] toDoubleArrayDouble(Collection<?> doublesCol) {
        double[] doubles = new double[doublesCol.size()];
        int i = 0;

        for (Object object : doublesCol) {
            Double aDouble = (Double) object;
            doubles[i] = aDouble;
            i++;
        }

        return doubles;
    }

    public static double toDouble(Float aFloat) {
        return aFloat.doubleValue();
    }

    public static double toDouble(BigDecimal bigDecimal) {
        return bigDecimal.doubleValue();
    }

    public static int toInteger(Short sShort) {
        return sShort.intValue();
    }

    public static int toInteger(Integer integer) {
        return integer;
    }

    public static int toInteger(Long aLong) {
        log.info("Casting long %s to an int value. You may loose information here.", aLong);
        return aLong.intValue();
    }

    public static int toInteger(Byte aByte) {
        return aByte.intValue();
    }
}
