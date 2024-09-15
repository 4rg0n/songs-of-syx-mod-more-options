package com.github.argon.sos.mod.sdk.booster;

import com.github.argon.sos.mod.sdk.util.MathUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterMapper {

    public static double normalizeBoost(BoostMode boostMode, int boost, double from, double to, double scale) {
        double newBoost = boost / (from + (to - from));

        return normalize(boostMode, newBoost, scale);
    }

    public static double normalize(BoostMode boostMode, double value, double scale) {
        value = value * scale;

        switch (boostMode) {
            default:
            case ADD:
            case MULTI:
                return value;
            case PERCENT:
                return MathUtil.toPercentage(value);
        }
    }
}
