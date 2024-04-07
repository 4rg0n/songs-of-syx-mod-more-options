package com.github.argon.sos.moreoptions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtil {
    public static double toPercentage(int value) {
        return (double) value / 100;
    }

    public static double toPercentage(double value) {
        return value / 100;
    }

    public static int fromPercentage(double percentage) {
        return fromResolution(percentage, 2);
    }

    public static int fromResolution(double percentage, int resolution) {
        return (int) (percentage * Math.pow(10, resolution));
    }

    public static double toResolution(double percentage, int resolution) {
        return (int) (percentage / Math.pow(10, resolution));
    }

    public static int precisionMulti(int resolution) {
        return (int) (Math.pow(10, resolution) / 10);
    }

    public static int nearest(int value, Collection<Integer> allowedValues) {
        return nearest(value, allowedValues.stream()
            .mapToInt(Integer::intValue)
            .toArray());
    }

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
