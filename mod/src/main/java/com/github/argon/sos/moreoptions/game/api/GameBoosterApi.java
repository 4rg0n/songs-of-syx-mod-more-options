
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.mod.sdk.booster.Boosters;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.moreoptions.booster.BoosterService;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import game.boosting.BoostableCat;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * Access to the mods custom {@link Boosters}
 */
@RequiredArgsConstructor
public class GameBoosterApi implements Phases, Resettable {

    private final BoosterService boosterService;

    @Override
    public void reset() {
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
}
