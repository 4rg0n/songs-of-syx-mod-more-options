package com.github.argon.sos.mod.sdk.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.boosting.BoosterImp;
import game.faction.player.Player;
import lombok.Getter;
import settlement.army.div.Div;
import settlement.stats.Induvidual;
import world.map.regions.Region;

public abstract class AbstractBooster extends BoosterImp {

    protected final double noValue;

    @Getter
    private final Boostable origin;

    protected final BoostMode boostMode;

    protected final double scale;

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
                this.noValue = 0.01D * this.scale ;
                break;
            case MULTI:
                this.noValue = this.scale;
                break;
            default:
            case ADD:
                this.noValue = 0.0D;
                break;
        }

        this.origin = origin;
    }

    @Override
    public double vGet(Induvidual indu) {
        return noValue;
    }

    @Override
    public double vGet(Region reg) {
        return noValue;
    }

    @Override
    public double vGet(Div div) {
        return noValue;
    }

    @Override
    public double vGet(PopTime popTime) {
        return noValue;
    }

    @Override
    public double vGet(Player f) {
        return noValue;
    }
}
