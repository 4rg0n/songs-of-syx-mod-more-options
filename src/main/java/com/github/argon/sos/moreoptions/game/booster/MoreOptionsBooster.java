package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.boosting.BoosterImp;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import settlement.army.Div;
import settlement.stats.Induvidual;
import world.regions.Region;

/**
 * Used for extending and therefore boosting game boosters
 */
public class MoreOptionsBooster extends BoosterImp {

    private double value;

    private final double initValue;

    private final double noValue;

    @Getter
    private final Boostable origin;

    @Builder
    public MoreOptionsBooster(
        Boostable origin,
        BSourceInfo bSourceInfo,
        double value,
        double from,
        double to,
        boolean isMul
    ) {
        super(bSourceInfo, from, to, isMul);
        value = normalize(value);

        if (isMul) {
            // will result in multiplying by 1
            this.noValue = 0.1D;
        } else {
            this.noValue = 0.0D;
        }

        this.initValue = value;
        this.value = value;
        this.origin = origin;
    }

    public void reset() {
        value = initValue;
    }

    public void set(double value) {
        this.value = normalize(value);
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
            return noValue;
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
        return noValue;
    }

    @Override
    public double vGet(Faction f) {
        if (FACTIONS.player().equals(f)) {
            return value;
        }
        else {
            return noValue;
        }
    }

    private double normalize(double value) {

        double newValue = value;

        // cap value at from, to and zero
        if (value > to()) {
            newValue = to();
        } else if (value < from()) {
            newValue = from();
        } else if (value < 0) {
            newValue = 0;
        }

        newValue = newValue / (from() + (to() - from()));

        return newValue;
    }

    public static MoreOptionsBooster fromRange(
        Boostable origin,
        BSourceInfo bSourceInfo,
        MoreOptionsConfig.Range range
    ) {
        if (range.getApplyMode().equals(MoreOptionsConfig.Range.ApplyMode.MULTI)) {
            return MoreOptionsBooster.builder()
                .bSourceInfo(bSourceInfo)
                .origin(origin)
                .from(BoosterUtil.toBoosterValue(range.getMin()))
                .to(BoosterUtil.toBoosterValue(range.getMax()))
                .value(MathUtil.toPercentage(range.getValue()))
                .isMul(true)
                .build();
        } else {
            return MoreOptionsBooster.builder()
                .bSourceInfo(bSourceInfo)
                .origin(origin)
                .from(BoosterUtil.toBoosterValue(range.getMin()))
                .to(BoosterUtil.toBoosterValue(range.getMax()))
                .value(BoosterUtil.toBoosterValue(range.getValue()))
                .isMul(false)
                .build();
        }
    }
}
