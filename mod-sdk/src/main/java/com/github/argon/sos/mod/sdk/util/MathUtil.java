package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtil {

    /**
     * @return e.g. 100 will be 1.0
     */
    public static double toPercentage(int value) {
        return (double) value / 100;
    }

    /**
     * @return e.g. 100.0 will be 1.0
     */
    public static double toPercentage(double value) {
        return value / 100;
    }

    /**
     * @return e.g. 1.0 will be 100
     */
    public static int fromPercentage(double percentage) {
        return fromResolution(percentage, 2);
    }

    /**
     * @return e.g. 1.0 with a resolution of 3 will be 1000
     */
    public static int fromResolution(double percentage, int resolution) {
        return (int) (percentage * Math.pow(10, resolution));
    }

    /**
     * @return e.g. 1.000 with a resolution of 3 will be 1.0
     */
    public static double toResolution(double percentage, int resolution) {
        return (int) (percentage / Math.pow(10, resolution));
    }

    public static int precisionMulti(int resolution) {
        return (int) (Math.pow(10, resolution) / 10);
    }

    /**
     * @return the value from the list, which is closed to the given value
     */
    public static int nearest(int value, Collection<Integer> allowedValues) {
        return nearest(value, allowedValues.stream()
            .mapToInt(Integer::intValue)
            .toArray());
    }

    /**
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
}
