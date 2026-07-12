package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import init.paths.ModInfo;
import init.paths.PATHS;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Access to the games mod information
 */
@RequiredArgsConstructor
public class GameModApi {
    private final static Logger log = Loggers.getLogger(GameModApi.class);

    /**
     * Returns all currently enabled mods as {@link ModInfo}s
     *
     * @return list of enabled mods
     */
    public List<ModInfo> getCurrentMods() {
        return Lists.fromGameLIST(PATHS.currentMods());
    }

    /**
     * Returns the {@link ModInfo} of a mod by the given name.
     *
     * @param modName of the mod
     * @return found mod info
     */
    public Optional<ModInfo> getModByName(String modName) {
        return getCurrentMods().stream().filter(modInfo -> {
            log.trace("Checking mod %s in %s", modInfo.name, modInfo.absolutePath);

            return modInfo.name.contains(modName);
        }).findFirst();
    }
}
