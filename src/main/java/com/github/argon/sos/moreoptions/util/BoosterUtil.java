package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBooster;
import game.boosting.BSourceInfo;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterUtil {

    public static MoreOptionsBooster extendAsMultiBooster(Boostable booster, MoreOptionsConfig.Range range) {
        return extendAsMultiBooster(booster, range.getValue(), range.getMin(), range.getMax());
    }

    public static MoreOptionsBooster extendAsAddBooster(Boostable booster, MoreOptionsConfig.Range range) {
        return extendAsAddBooster(booster, range.getValue(), range.getMin(), range.getMax());
    }

    public static MoreOptionsBooster extendAsAddBooster(Boostable booster, int value, int min, int max) {
        MoreOptionsBooster moreOptionsBooster = new MoreOptionsBooster(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name, SPRITES.icons().m.cog),
            (double) value / 100, (double) min / 100, (double) max / 100, false);

        BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return moreOptionsBooster;
    }

    public static MoreOptionsBooster extendAsMultiBooster(Boostable booster, int value, int min, int max) {
        double boosterValue = MathUtil.toPercentage(value) / 100;
        double boosterMin = MathUtil.toPercentage(min) / 100;
        double boosterMax = MathUtil.toPercentage(max) / 100;

        MoreOptionsBooster moreOptionsBooster = new MoreOptionsBooster(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name, SPRITES.icons().m.cog),
            boosterValue, boosterMin, boosterMax, true);

        BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return moreOptionsBooster;
    }

    public static boolean alreadyExtended(Boostable booster, boolean isMul) {
        if (isMul) {
            for (BoostSpec mul : booster.muls()) {
                if (mul.tName.toString().contains(MoreOptionsScript.MOD_INFO.name)) {
                    return true;
                }
            }
        } else {
            for (BoostSpec add : booster.adds()) {
                if (add.tName.toString().contains(MoreOptionsScript.MOD_INFO.name)) {
                    return true;
                }
            }
        }

        return false;
    }


}
