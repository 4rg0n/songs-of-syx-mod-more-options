package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import game.boosting.BOOSTABLES;
import game.boosting.BSourceInfo;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import game.faction.npc.ruler.ROpinions;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsBoosterFactory {

    private final static Logger log = Loggers.getLogger(BoosterService.class);

    public final static String KEY_PREFIX = "booster";

    public static Map<String, MoreOptionsBoosters> createDefault(Map<String, MoreOptionsBoosters> presentBoosters) {
        Map<String, MoreOptionsBoosters> boosters = new HashMap<>();

        String opinionKey = key(ROpinions.GET().key);
        MoreOptionsBoosters opinionBoosters = prepareBooster(ROpinions.GET(), presentBoosters.get(key(opinionKey)));
        boosters.put(opinionKey, opinionBoosters);

        BOOSTABLES.CIVICS().all().forEach(booster -> boosters.put(
            key(booster.key),
            prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        BOOSTABLES.BATTLE().all().forEach(booster -> boosters.put(
            key(booster.key),
            prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        BOOSTABLES.BEHAVIOUR().all().forEach(booster -> boosters.put(
            key(booster.key),
            prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        BOOSTABLES.PHYSICS().all().forEach(booster -> boosters.put(
            key(booster.key),
            prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        BOOSTABLES.ROOMS().all().forEach(booster -> boosters.put(
            key(booster.key),
            prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        BOOSTABLES.START().all().forEach(booster -> boosters.put(
                key(booster.key),
                prepareBooster(booster, presentBoosters.get(key(booster.key)))));

        log.debug("Built %s default boosters", boosters.size());

        return boosters;
    }

    private static MoreOptionsBoosters prepareBooster(Boostable booster, @Nullable MoreOptionsBoosters presentBooster) {
        MoreOptionsBooster boosterMulti;
        if (presentBooster != null && BoosterUtil.alreadyExtended(booster, true)) {
            log.trace("Reuse present multi booster: %s", presentBooster.getMulti().info.name);
            boosterMulti = presentBooster.getMulti();
        } else {
             boosterMulti = createMoreOptionsBooster(booster, ConfigDefaults.boosterMulti());
        }

        MoreOptionsBooster boosterAdd;
        if (presentBooster != null && BoosterUtil.alreadyExtended(booster, false)) {
            log.trace("Reuse present add booster: %s", presentBooster.getAdd().info.name);
            boosterAdd = presentBooster.getAdd();
        }  else {
            boosterAdd = createMoreOptionsBooster(booster, ConfigDefaults.boosterAdd());
        }

        return MoreOptionsBoosters.builder()
            .add(boosterAdd)
            .multi(boosterMulti)
            .build();
    }

    public static MoreOptionsBooster createMoreOptionsBooster(Boostable booster, MoreOptionsV2Config.Range range) {
        String suffix = " Perc";

        if (range.getApplyMode().equals(MoreOptionsV2Config.Range.ApplyMode.ADD)) {
            suffix = " Add";
        }

        MoreOptionsBooster moreOptionsBooster = MoreOptionsBooster.fromRange(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name + suffix, SPRITES.icons().m.cog),
            range
        );

        BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return moreOptionsBooster;
    }

    private static String key(String key) {
        return KEY_PREFIX + "." + key;
    }
}
