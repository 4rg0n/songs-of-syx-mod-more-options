package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Utility class with mathematical operations.
 */
@UtilityClass
public class MathUtil {

    /**
     * Sums all {@link Integer}s in a list.
     *
     * @param integers to sum
     * @return sum of all integers in the list
     */
    public static int sum(@Nullable Collection<Integer> integers) {
        if (integers == null) {
            return 0;
        }

        return integers.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Calculates a percentage value from an integer.
     *
     * @param value to calculate the percentage value from
     * @return e.g. 100 will be 1.0
     */
    public static double toPercentage(int value) {
        return (double) value / 100;
    }

    /**
     * Calculates a percentage value from a double.
     *
     * @param value to calculate the percentage value from
     * @return e.g. 100.0 will be 1.0
     */
    public static double toPercentage(double value) {
        return value / 100;
    }

    /**
     * Calculates an integer from a double percentage value.
     *
     * @param percentage to calculate the value from
     * @return e.g. 1.0 will be 100
     */
    public static int fromPercentage(double percentage) {
        return fromResolution(percentage, 2);
    }

    /**
     * Calculates the int value for a given resolution.
     *
     * @param value to use
     * @param resolution used to calculate
     * @return e.g. 1.0 with a resolution of 3 will be 1000
     */
    public static int fromResolution(double value, int resolution) {
        return (int) (value * Math.pow(10, resolution));
    }

    /**
     * Calculates the resolution for a double for decimals.
     *
     * @param value to use
     * @param resolution used to calculate
     * @return e.g. 1.000 with a resolution of 3 will be 1.0
     */
    public static double toResolution(double value, int resolution) {
        return (int) (value / Math.pow(10, resolution));
    }

    /**
     * Calculates the multiplier used for decimal precisions.
     *
     * @param resolution to use
     * @return calculated multiplier
     */
    public static int precisionMulti(int resolution) {
        return (int) (Math.pow(10, resolution) / 10);
    }

    /**
     * Finds the nearest integer value in a list of integer values.
     *
     * @param value to look for
     * @param allowedValues list with values to search through
     * @return the value from the list, which is closed to the given value
     */
    public static int nearest(int value, Collection<Integer> allowedValues) {
        return nearest(value, allowedValues.stream()
            .mapToInt(Integer::intValue)
            .toArray());
    }

    /**
     * Finds the nearest integer value in an array of integer values.
     *
     * @param value to look for
     * @param allowedValues array with values to search through
     * @return the value from the list, which is closed to the given value
     */
    public static int nearest(int value, int[] allowedValues) {
        int distance = Math.abs(allowedValues[0] - value);
        int idx = 0;
        for(int c = 1; c < allowedValues.length; c++){
            int cdistance = Math.abs(allowedValues[c] - value);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }

        return allowedValues[idx];
    }

    /**
     * Finds the nearest double value in an array of double values.
     *
     * @param value to look for
     * @param allowedValues array with values to search through
     * @return the value from the list, which is closed to the given value
     */
    public static double nearest(double value, Collection<Double> allowedValues) {
        return nearest(value, allowedValues.stream()
            .mapToDouble(Double::doubleValue)
            .toArray());
    }

    /**
     * Finds the nearest double value in an array of double values.
     *
     * @param value to look for
     * @param allowedValues array with values to search through
     * @return the value from the list, which is closed to the given value
     */
    public static double nearest(double value, double[] allowedValues) {
        double distance = Math.abs(allowedValues[0] - value);
        int idx = 0;
        for(int c = 1; c < allowedValues.length; c++){
            double cdistance = Math.abs(allowedValues[c] - value);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }

        return allowedValues[idx];
    }
}
