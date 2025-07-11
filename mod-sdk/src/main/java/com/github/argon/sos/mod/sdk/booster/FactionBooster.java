package com.github.argon.sos.mod.sdk.booster;

import game.boosting.BOOSTABLE_O;
import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.player.Player;
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
        BoostMode boostMode
    ) {
        super(origin, bSourceInfo, min, max, boostMode);
    }

    @Override
    public double vGet(Faction f) {
        return getFactionValue(f);
    }

    @Override
    public double vGet(Player player) {
        return getFactionValue(player);
    }

    @Override
    public double get(BOOSTABLE_O o) {
        if (o instanceof Player) {
            return vGet((Player) o);
        }

        if (o instanceof Faction) {
            return vGet((Faction) o);
        }

        return super.get(o);
    }

    public void set(Faction faction, int value) {
        double scaled = value * scale;
        factionBoosts.put(faction, scaled);
    }

    public void reset() {
        factionBoosts.entrySet().forEach(entry -> entry.setValue(noValue));
    }

    private double getFactionValue(Faction faction) {
        Double boost = factionBoosts.get(faction);

        if (boost == null) {
            return noValue;
        }

        return boost;
    }
}
