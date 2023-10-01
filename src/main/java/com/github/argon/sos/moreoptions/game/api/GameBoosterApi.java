
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import init.boostable.BOOSTABLE;
import init.boostable.BOOSTABLES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    private Map<String, BOOSTABLE> boosters;

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi();

    public void clearCached() {
        boosters = null;
    }

    public Map<String, BOOSTABLE> getBoosters() {
        if (boosters == null) {
            boosters = new HashMap<>();
            BOOSTABLES.all().forEach(boostable -> {
                boosters.put("booster." + boostable.key, boostable);
            });
        }

        return boosters;
    }

    public void setBoostValue(BOOSTABLE boostable, int boost) {
        double currentValue = boostable.defAdd;
        double newValue = currentValue * MathUtil.toPercentage(boost);

        log.trace("Applying boost value %s%% to %s = %s", boost, boostable.key, newValue);
        boostable.setDefAdd(newValue);
    }
}
