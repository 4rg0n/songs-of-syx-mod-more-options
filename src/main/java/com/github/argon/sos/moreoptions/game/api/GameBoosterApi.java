
package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.booster.BoosterService;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBoosters;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.init.UninitializedException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import game.boosting.BoostableCat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameBoosterApi implements InitPhases {

    private final static Logger log = Loggers.getLogger(GameBoosterApi.class);

    public final static String KEY_PREFIX = "booster";

    @Getter(lazy = true)
    private final static GameBoosterApi instance = new GameBoosterApi(
        BoosterService.getInstance()
    );

    private final BoosterService boosterService;

    public void clearCached() {
        boosterService.reset();
    }

    public Map<String, MoreOptionsBoosters> getBoosters() {
        return boosterService.getBoosters()
            .orElseThrow(UninitializedException::new);
    }

    public MoreOptionsBoosters get(String key) {
        return boosterService.get(key)
            .orElseThrow(UninitializedException::new);
    }

    public BoostableCat getCat(String key) {
        return boosterService.getCat(key)
            .orElseThrow(UninitializedException::new);
    }

    public void setBoosters(Map<String, MoreOptionsV2Config.Range> ranges) {
        boosterService.setBoosterValues(ranges);
    }

    @Override
    public void initCreateInstance() {
        log.debug("Init game booster api");
        boosterService.reset();
    }
}
