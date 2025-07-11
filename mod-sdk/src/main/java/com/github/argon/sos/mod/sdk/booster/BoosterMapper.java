package com.github.argon.sos.mod.sdk.booster;

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
            case ADD:
            case MULTI:
            case PERCENT:
            default:
                return value;
        }
    }
}
