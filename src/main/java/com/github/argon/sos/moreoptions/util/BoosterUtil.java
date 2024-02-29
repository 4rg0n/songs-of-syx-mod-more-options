package com.github.argon.sos.moreoptions.util;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterUtil {


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

    public static double toBoosterValue(int value) {
        return (double) value / 1000;
    }
}
