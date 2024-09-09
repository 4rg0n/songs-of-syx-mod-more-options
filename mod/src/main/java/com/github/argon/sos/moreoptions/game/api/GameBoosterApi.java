
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.booster.Boosters;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import game.boosting.BoostableCat;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * Access to the mods custom {@link Boosters}
 */
@RequiredArgsConstructor
public class GameBoosterApi implements Phases {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    private final BoosterService boosterService;

    public void clearCached() {
        boosterService.reset();
    }

    public Map<String, Boosters> getBoosters() {
        return boosterService.getBoosters()
            .orElseThrow(() -> new UninitializedException(Phase.INIT_MOD_CREATE_INSTANCE));
    }

    public Optional<Boosters> get(String key) {
        return boosterService.get(key);
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

    @Override
    public void onGameSaveReloaded() {
        clearCached();
    }
}
