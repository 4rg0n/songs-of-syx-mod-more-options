package com.github.argon.sos.moreoptions.game.booster;

import game.boosting.BSourceInfo;
import game.boosting.BoosterImp;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.Race;
import settlement.army.Div;
import settlement.stats.Induvidual;
import world.regions.Region;

public class MoreOptionsBooster extends BoosterImp {

    private double value = 0.0;

    private int max = 10000; //default

    public MoreOptionsBooster(BSourceInfo bSourceInfo, double from, double to, boolean isMul) {
        super(bSourceInfo, from, to, isMul);
    }

    public void set(double value) {
        this.value = value;
    }
    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public double vGet(Region reg) {
        return value / max;
    }

    @Override
    public double vGet(Induvidual indu) {
        return value / max;
    }

    @Override
    public double vGet(Div div) {
        return value / max;
    }

    @Override
    public double vGet(POP_CL reg) {
        if (reg.cl != null && !reg.cl.player)
        {
            return 0;
        }
        else {
            return value / max;
        }
    }

    @Override
    public double vGet(Royalty roy) {
        return value / max;
    }

    @Override
    public double vGet(Race race) {
        return value / max;
    }

    @Override
    public double vGet(NPCBonus bonus) {
        return 0;
    }

    @Override
    public double vGet(Faction f) {
        if(FACTIONS.player().equals(f)) {
            return value / max;
        }
        else {
            return 0;
        }
    }
}
