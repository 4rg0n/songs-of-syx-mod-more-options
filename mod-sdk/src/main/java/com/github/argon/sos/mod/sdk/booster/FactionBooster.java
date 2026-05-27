package com.github.argon.sos.mod.sdk.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.FactionNPC;
import game.faction.player.Player;
import game.faction.royalty.Royalty;
import init.type.HCLASS_RACE;
import lombok.Builder;
import game.battle.div.Div;
import settlement.stats.Induvidual;
import world.map.regions.Region;

import java.util.HashMap;
import java.util.Map;

/**
 * For boosters applicable to multiple factions.
 */
public class FactionBooster extends AbstractBooster {

    private final Map<Faction, Double> factionBoosts = new HashMap<>();

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
    public double vGet(HCLASS_RACE populationClass) {
        if (populationClass.f() == null) {
            return vGet(FACTIONS.player());
        } else {
            return vGet((Faction) populationClass.f());
        }
    }

    @Override
    public double vGet(FactionNPC f) {
        return getFactionValue(f);
    }

    @Override
    public double vGet(Induvidual induvidual) {
        return super.vGet(induvidual.faction());
    }

    @Override
    public double vGet(Region region) {
        return super.vGet(region.faction());
    }

    @Override
    public double vGet(Royalty royalty) {
        return super.vGet(royalty.induvidual.faction());
    }

    @Override
    public double vGet(Div division) {
        return super.vGet(division.faction());
    }

    @Override
    public double getValue(double d) {
        return d;
    }

    public void set(Faction faction, int value) {
        double scaled = value * scale;
        factionBoosts.put(faction, scaled);
    }

    public void reset() {
        factionBoosts.forEach((key, value) -> reset(key));
    }

    public void reset(Faction faction) {
        factionBoosts.put(faction, noValue);
    }

    private double getFactionValue(Faction faction) {
        Double boost = factionBoosts.get(faction);

        if (boost == null) {
            return noValue;
        }

        return boost;
    }
}
