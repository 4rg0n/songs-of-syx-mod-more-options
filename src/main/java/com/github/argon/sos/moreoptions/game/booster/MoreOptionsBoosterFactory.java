package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import game.faction.npc.ruler.ROpinions;
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
             boosterMulti = BoosterUtil.extendAsMultiBooster(booster, MoreOptionsConfig.Range.builder()
                 .min(1)
                 .value(100)
                 .build());
        }

        MoreOptionsBooster boosterAdd;
        if (presentBooster != null && BoosterUtil.alreadyExtended(booster, false)) {
            log.trace("Reuse present add booster: %s", presentBooster.getAdd().info.name);
            boosterAdd = presentBooster.getAdd();
        }  else {
            boosterAdd = BoosterUtil.extendAsAddBooster(booster, MoreOptionsConfig.Range.builder()
                .build());
        }

        return MoreOptionsBoosters.builder()
            .add(boosterAdd)
            .multi(boosterMulti)
            .build();
    }

    private static String key(String key) {
        return KEY_PREFIX + "." + key;
    }
}
