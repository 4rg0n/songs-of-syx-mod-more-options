package com.github.argon.sos.moreoptions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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


}
