package com.github.argon.sos.moreoptions.game.booster;

import game.boosting.BoosterImp;
import game.faction.npc.ruler.Royalty;

public class FactionOpinionBooster extends BoosterImp<Royalty> {

    private double value = 0.0;

    public FactionOpinionBooster(CharSequence sourceName, double from, double to, boolean isMul) {
        super(sourceName, from, to, isMul);
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public double get(Royalty royalty) {
        return value;
    }
}
