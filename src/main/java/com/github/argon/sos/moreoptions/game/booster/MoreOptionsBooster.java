package com.github.argon.sos.moreoptions.game.booster;

import game.boosting.BSourceInfo;
import game.boosting.BoosterImp;
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

    public MoreOptionsBooster(BSourceInfo bSourceInfo, double from, double to, boolean isMul) {
        super(bSourceInfo, from, to, isMul);
    }

    public void set(double value) {
        this.value = value;
    }


    @Override
    public double vGet(Region reg) {
        return value / 10000;
    }

    @Override
    public double vGet(Induvidual indu) {
        return value / 10000;
    }

    @Override
    public double vGet(Div div) {
        return value / 10000;
    }

    @Override
    public double vGet(POP_CL reg) {
        return value / 10000;
    }

    @Override
    public double vGet(Royalty roy) {
        return value / 10000;
    }

    @Override
    public double vGet(Race race) {
        return value / 10000;
    }

    @Override
    public double vGet(NPCBonus bonus) {
        return value / 10000;
    }

    @Override
    public double vGet(Faction f) {
        return value / 10000;
    }
}
