package com.github.argon.sos.moreoptions.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.boosting.BoosterImp;
import lombok.Getter;

public abstract class AbstractBooster extends BoosterImp {

    protected final double noValue;

    @Getter
    private final Boostable origin;

    protected final BoostMode boostMode;

    protected double scale = 1.0D;

    public AbstractBooster(
        Boostable origin,
        BSourceInfo bSourceInfo,
        int min,
        int max,
        double scale,
        BoostMode boostMode
    ) {
        super(
            bSourceInfo,
            BoosterMapper.normalize(boostMode, min, scale),
            BoosterMapper.normalize(boostMode, max, scale),
            !boostMode.equals(BoostMode.ADD)
        );
        this.boostMode = boostMode;
        this.scale = scale;

        switch (boostMode) {
            case PERCENT:
                this.noValue = 0.01D;
                break;
            case MULTI:
                this.noValue = 1.0D;
                break;
            default:
            case ADD:
                this.noValue = 0.0D;
                break;
        }

        this.origin = origin;
    }
}
