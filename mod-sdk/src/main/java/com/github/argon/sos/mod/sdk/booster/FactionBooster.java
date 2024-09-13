package com.github.argon.sos.mod.sdk.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import game.faction.Faction;
import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

/**
 * For boosters applicable to multiple factions.
 */
public class FactionBooster extends AbstractBooster {

    private final Map<Faction, Double> factionBoosts = new HashMap<>(FACTIONS.MAX);

    @Builder
    public FactionBooster(
        Boostable origin,
        BSourceInfo bSourceInfo,
        int min,
        int max,
        double scale,
        BoostMode boostMode
    ) {
        super(origin, bSourceInfo, min, max, scale, boostMode);
    }

    @Override
    public double vGet(Faction f) {
        Double boost = factionBoosts.get(f);

        if (boost == null) {
            return noValue;
        }

        return boost;
    }

    public void set(Faction faction, int value) {
        factionBoosts.put(faction, BoosterMapper.normalizeBoost(boostMode, value, from(), to(), scale));
    }

    public void reset() {
        factionBoosts.entrySet().forEach(entry -> entry.setValue(noValue));
    }
}
