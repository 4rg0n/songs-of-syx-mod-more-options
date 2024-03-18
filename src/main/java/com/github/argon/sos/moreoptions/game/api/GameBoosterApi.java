
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.booster.Boosters;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import game.boosting.BoostableCat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Access to the mods custom {@link Boosters}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi implements Phases {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi(
        BoosterService.getInstance()
    );

    private final BoosterService boosterService;

    public void clearCached() {
        boosterService.reset();
    }

    public Map<String, Boosters> getBoosters() {
        return boosterService.getBoosters()
            .orElseThrow(() -> new UninitializedException(Phase.INIT_MOD_CREATE_INSTANCE));
    }

    public Boosters get(String key) {
        return boosterService.get(key)
            .orElseThrow(() -> new UninitializedException(Phase.INIT_MOD_CREATE_INSTANCE));
    }

    public BoostableCat getCat(String key) {
        return boosterService.getCat(key)
            .orElseThrow(() -> new UninitializedException(Phase.INIT_MOD_CREATE_INSTANCE));
    }

    public void setBoosters(BoostersConfig boostersConfig) {
        boosterService.setBoosterValues(boostersConfig);
    }

    @Override
    public void initModCreateInstance() {
        boosterService.reset();
    }
}
