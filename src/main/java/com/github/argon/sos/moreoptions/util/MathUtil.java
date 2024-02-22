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
}
