package com.github.argon.sos.mod.sdk.data.save;

import com.github.argon.sos.mod.sdk.ModSdkModule;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GameSavers {
    private final static Map<Path, GameSaver> savers = new HashMap<>();

    public static GameSaver getSaver(Path path) {
        if (!savers.containsKey(path)) {
            GameSaver gameSaver = ModSdkModule.Factory.newGameSaver(path);
            savers.put(path, gameSaver);
        }

        return savers.get(path);
    }
}
