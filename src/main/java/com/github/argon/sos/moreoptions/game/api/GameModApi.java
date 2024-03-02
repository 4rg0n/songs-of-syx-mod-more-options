package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import init.paths.ModInfo;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameModApi {
    private final static Logger log = Loggers.getLogger(GameModApi.class);

    @Getter(lazy = true)
    private final static GameModApi instance = new GameModApi();

    public List<ModInfo> getCurrentMods() {
        return Lists.listFromLIST(PATHS.currentMods());
    }

    public Path getCurrentModPath() {
        Path path = PATHS.SCRIPT().jar.get().toAbsolutePath();
        return path.getParent().getParent();
    }

    public Optional<ModInfo> getCurrentMod() {
        return getCurrentMods().stream().filter(modInfo -> {
            log.trace("Checking mod %s in %s", modInfo.name, modInfo.absolutePath);

            Path currentModPath = getCurrentModPath();
            return currentModPath.startsWith(modInfo.absolutePath);
        }).findFirst();
    }
}
