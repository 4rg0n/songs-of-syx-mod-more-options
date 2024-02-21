package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import game.boosting.BOOSTABLES;
import game.boosting.Boostable;
import game.faction.npc.ruler.ROpinions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsBoosterFactory {

    public final static String KEY_PREFIX = "booster";
    public static Map<String, MoreOptionsBoosters> createDefault() {
        Map<String, MoreOptionsBoosters> boosters = new HashMap<>();

        String opinionKey = key(ROpinions.GET().key);
        MoreOptionsBoosters opinionBoosters = prepareBooster(ROpinions.GET());
        boosters.put(opinionKey, opinionBoosters);

        BOOSTABLES.CIVICS().all().forEach(booster -> {
            boosters.put(
                key(booster.key),
                prepareBooster(booster)
            );
        });
        BOOSTABLES.BATTLE().all().forEach(booster -> {
            boosters.put(
                key(booster.key),
                prepareBooster(booster)
            );
        });
        BOOSTABLES.BEHAVIOUR().all().forEach(booster -> {
            boosters.put(
                key(booster.key),
                prepareBooster(booster)
            );
        });
        BOOSTABLES.PHYSICS().all().forEach(booster -> {
            boosters.put(
                key(booster.key),
                prepareBooster(booster)
            );
        });
        BOOSTABLES.ROOMS().all().forEach(booster -> {
            boosters.put(
                key(booster.key),
                prepareBooster(booster)
            );
        });
//        BOOSTABLES.START().all().forEach(booster -> {
//            boosters.put(
//                key(booster.key),
//                prepareBooster(booster)
//            );
//        });

        return boosters;
    }

    private static MoreOptionsBoosters prepareBooster(Boostable booster) {
        return MoreOptionsBoosters.builder()
            .additive(BoosterUtil.extendAsAddBooster(booster, MoreOptionsConfig.Range
                .builder()
                .build()))
            .multi(BoosterUtil.extendAsMultiBooster(booster, MoreOptionsConfig.Range.builder()
                .value(100)
                .build()))
            .build();
    }

    private static String key(String key) {
        return KEY_PREFIX + "." + key;
    }
}
