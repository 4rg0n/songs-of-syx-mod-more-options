package com.github.argon.sos.moreoptions.game.booster;

import game.boosting.BSourceInfo;
import game.boosting.BoosterImp;
import game.faction.Faction;
import game.faction.npc.ruler.Royalty;

public class FactionOpinionBooster extends BoosterImp {

    private double value = 0.0;

    public FactionOpinionBooster(BSourceInfo bSourceInfo, double from, double to, boolean isMul) {
        super(bSourceInfo, from, to, isMul);
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public double vGet(Faction f) {
        return 0;
    }
}
