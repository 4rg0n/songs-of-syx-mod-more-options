package com.github.argon.sos.moreoptions.game.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.boosting.BoosterImp;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.Race;
import lombok.Getter;
import settlement.army.Div;
import settlement.stats.Induvidual;
import world.regions.Region;

public class MoreOptionsBooster extends BoosterImp {

    private double value;


    private final double initValue;

    @Getter
    private final Boostable origin;

    public MoreOptionsBooster(Boostable origin, BSourceInfo bSourceInfo, double value, double from, double to, boolean isMul) {
        super(bSourceInfo, from, to, isMul);
        this.initValue = value;
        this.value = value;
        this.origin = origin;
    }

    public void reset() {
        value = initValue;
    }

    public void set(double value) {
        this.value = value;
    }
    @Override
    public double vGet(Region reg) {
        return value;
    }

    @Override
    public double vGet(Induvidual indu) {
        return value;
    }

    @Override
    public double vGet(Div div) {
        return value;
    }

    @Override
    public double vGet(POP_CL reg) {
        if (reg.cl != null && !reg.cl.player)
        {
            return 0;
        }
        else {
            return value;
        }
    }

    @Override
    public double vGet(Royalty roy) {
        return value;
    }

    @Override
    public double vGet(Race race) {
        return value;
    }

    @Override
    public double vGet(NPCBonus bonus) {
        return 0;
    }

    @Override
    public double vGet(Faction f) {
        if (FACTIONS.player().equals(f)) {
            return value;
        }
        else {
            return 0;
        }
    }
}
