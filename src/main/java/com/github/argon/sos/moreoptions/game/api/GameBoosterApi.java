
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import init.boostable.BOOSTABLE;
import init.boostable.BOOSTABLES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    private Map<String, BOOSTABLE> allBoosters;
    private Map<String, BOOSTABLE> enemyBoosters;
    private Map<String, BOOSTABLE> playerBoosters;

    public final static String KEY_PREFIX = "booster";

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi();

    public void clearCached() {
        allBoosters = null;
    }

    public Map<String, BOOSTABLE> getAllBoosters() {
        if (allBoosters == null) {
            allBoosters = new HashMap<>();
            BOOSTABLES.all().forEach(boostable -> {
                allBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }

        return allBoosters;
    }

    public Map<String, BOOSTABLE> getPlayerBoosters() {
        if (playerBoosters == null) {
            playerBoosters = new HashMap<>();
            BOOSTABLES.player().getBoosters().forEach(boostable -> {
                playerBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }

        return playerBoosters;
    }

    public Map<String, BOOSTABLE> getEnemyBoosters() {
        if (enemyBoosters == null) {
            enemyBoosters = new HashMap<>();
            BOOSTABLES.enemy().getBoosters().forEach(boostable -> {
                enemyBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }

        return enemyBoosters;
    }

    public boolean isEnemyBooster(String key) {
        return getEnemyBoosters().containsKey(key);
    }

    public boolean isPlayerBooster(String key) {
        return getPlayerBoosters().containsKey(key);
    }

    public void setBoosterValue(BOOSTABLE boostable, int boost) {
        double currentValue = boostable.defAdd;
        double newValue = currentValue * MathUtil.toPercentage(boost);

        log.trace("Applying boost value %s%% to %s = %s", boost, boostable.key, newValue);
        boostable.setDefAdd(newValue);
    }
}
