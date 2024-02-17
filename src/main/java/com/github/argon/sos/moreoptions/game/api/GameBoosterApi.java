
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.booster.FactionOpinionBooster;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBooster;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.boosting.*;
import game.faction.FACTIONS;
import game.faction.npc.ruler.ROpinions;
import init.race.RACES;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.sets.LIST;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    private Map<String, Boostable> allBoosters;
    private Map<String, Boostable> enemyBoosters;
    private Map<String, Boostable> playerBoosters;

    private Map<String, BoostableCat> catBoosters;

    public final static String KEY_PREFIX = "booster";

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi();

    public void clearCached() {
        allBoosters = null;
        playerBoosters = null;
        enemyBoosters = null;
        catBoosters = null;
    }

    public Map<String, Boostable> getAllBoosters() {
        if (allBoosters == null) {
            allBoosters = new HashMap<>();

            LIST<BoostSpec> adds = ROpinions.GET().adds();
            CharSequence charSequence = MoreOptionsScript.MOD_INFO.name;
            for(BoostSpec boostSpec : adds) {
                if(boostSpec.booster.info.name.equals(charSequence)){
                    allBoosters.put(KEY_PREFIX + "." + boostSpec.boostable.key, boostSpec.boostable);
                }
            }


            BOOSTABLES.CIVICS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.BATTLE().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.BEHAVIOUR().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.PHYSICS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
            BOOSTABLES.ROOMS().all().forEach(civ -> {allBoosters.put(KEY_PREFIX + "." + civ.key, civ);});
        }

        return allBoosters;
    }

    public Map<String, Boostable> getPlayerBoosters() {
        if (playerBoosters == null) {
            playerBoosters = new HashMap<>();

            BOOSTABLES.all().forEach(boostable -> { //TODO::differentiate player and enemy
                boostable.get(FACTIONS.player());
                playerBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }

        return allBoosters;
    }

    public Map<String, Boostable> getEnemyBoosters() {
        if (enemyBoosters == null) {
            enemyBoosters = new HashMap<>();
            BOOSTABLES.all().forEach(boostable -> {
                enemyBoosters.put(KEY_PREFIX + "." + boostable.key, boostable);
            });
        }
        return allBoosters;
    }

    public Map<String, BoostableCat> getCatBoosters() {
        if (catBoosters == null) {
            catBoosters = new HashMap<>();

            LIST<BoostSpec> adds = ROpinions.GET().adds();
            CharSequence charSequence = "More Options";
            for(BoostSpec boostSpec : adds) {
                if(boostSpec.booster.info.name.equals(charSequence)){
                    catBoosters.put(KEY_PREFIX + "." + boostSpec.boostable.key, boostSpec.boostable.cat);
                }
            }

            BOOSTABLES.CIVICS().all().forEach(civ -> catBoosters.put(KEY_PREFIX + "." + civ.key, civ.cat));
            BOOSTABLES.BATTLE().all().forEach(civ -> catBoosters.put(KEY_PREFIX + "." + civ.key, civ.cat));
            BOOSTABLES.BEHAVIOUR().all().forEach(civ -> catBoosters.put(KEY_PREFIX + "." + civ.key, civ.cat));
            BOOSTABLES.PHYSICS().all().forEach(civ -> catBoosters.put(KEY_PREFIX + "." + civ.key, civ.cat));
            BOOSTABLES.ROOMS().all().forEach(civ -> catBoosters.put(KEY_PREFIX + "." + civ.key, civ.cat));
        }
        return catBoosters;
    }


    public boolean isEnemyBooster(String key) {
        return getEnemyBoosters().containsKey(key);
    }

    public BoostableCat getCat(String key) {
        return getCatBoosters().get(key);
    }

    public boolean isPlayerBooster(String key) {
        return getPlayerBoosters().containsKey(key);
    }

    public void setBoosterValue(Boostable boostable, MoreOptionsConfig.Range range) {

        LIST<BoostSpec> adds = boostable.adds();
        CharSequence charSequence = "More Options";
        boolean isExists = false;
        for(BoostSpec boostSpec : adds) {
            if(boostSpec.boostable.equals(boostable)){
                if(boostSpec.booster.info.name.equals(charSequence)){
                    isExists = true;
                    ((MoreOptionsBooster) boostSpec.booster).set(range.getValue());
                }
            }
        }
        if (!isExists) {
            MoreOptionsBooster moreOptionsBooster = new MoreOptionsBooster(new BSourceInfo(MoreOptionsScript.MOD_INFO.name, SPRITES.icons().m.cog), 0, range.getMax(), false);
            moreOptionsBooster.set(range.getValue());
            moreOptionsBooster.setMax(range.getMax());
            BoostSpec boostSpec = new BoostSpec(moreOptionsBooster, boostable, charSequence);
            boostable.addFactor(boostSpec);
        }
    }
}
