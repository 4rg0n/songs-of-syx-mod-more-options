package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import init.paths.ModInfo;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Access to the games mod information
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameModApi {
    private final static Logger log = Loggers.getLogger(GameModApi.class);

    @Getter(lazy = true)
    private final static GameModApi instance = new GameModApi();

    public List<ModInfo> getCurrentMods() {
        return Lists.fromGameLIST(PATHS.currentMods());
    }

    public Optional<ModInfo> getCurrentMod(String currentModName) {
        return getCurrentMods().stream().filter(modInfo -> {
            log.trace("Checking mod %s in %s", modInfo.name, modInfo.absolutePath);

            return modInfo.name.contains(currentModName);
        }).findFirst();
    }
}
