package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterUtil {

    /**
     * Checks whether a game booster already contains a "More Options" booster
     */
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

    /**
     * A value of 0.001 means 1 in the games boosters
     */
    public static double toBoosterValue(int value) {
        return toBoosterValue((double) value);
    }

    /**
     * A value of 0.001 means 1.0 in the games boosters
     */
    public static double toBoosterValue(double value) {
        return value / 1000;
    }
}
