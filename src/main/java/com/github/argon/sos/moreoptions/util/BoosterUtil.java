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
        int min = range.getMax() / -2;
        return extendAsAddBooster(booster, range.getValue(), min, range.getMax());
    }

    public static MoreOptionsBooster extendAsAddBooster(Boostable booster, int value, int min, int max) {
        MoreOptionsBooster moreOptionsBooster = new MoreOptionsBooster(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name, SPRITES.icons().m.cog),
            value, min, max, false);

        BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return moreOptionsBooster;
    }

    public static MoreOptionsBooster extendAsMultiBooster(Boostable booster, int value, int min, int max) {
        double boosterValue = MathUtil.toPercentage(value);
        double boosterMin = MathUtil.toPercentage(min);
        double boosterMax = MathUtil.toPercentage(max);

        MoreOptionsBooster moreOptionsBooster = new MoreOptionsBooster(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name, SPRITES.icons().m.cog),
            boosterValue, boosterMin, boosterMax, true);

        BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return moreOptionsBooster;
    }
}
