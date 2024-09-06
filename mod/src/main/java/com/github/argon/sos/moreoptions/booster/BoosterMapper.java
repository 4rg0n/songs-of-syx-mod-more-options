package com.github.argon.sos.moreoptions.booster;

import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterMapper {
    public static FactionBooster fromRange(
        Boostable origin,
        BSourceInfo bSourceInfo,
        Range range,
        double scale
    ) {
        return FactionBooster.builder()
            .bSourceInfo(bSourceInfo)
            .origin(origin)
//            .from(BoosterUtil.toBoosterValue(range.getMin()))
//            .to(BoosterUtil.toBoosterValue(range.getMax()))
//            .value(MathUtil.toPercentage(range.getValue()))
            .min(range.getMin())
            .max(range.getMax())
            .scale(scale)
            .boostMode(BoostMode.valueOf(range.getApplyMode().name()))
            .build();
    }

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
