package com.github.argon.sos.mod.sdk.game.asset.init;

import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents the game "init/race" folder structure.
 */
public class InitRace extends AbstractGameData {
    /**
     * Creates a new "init/race" folder structure instance
     */
    public InitRace() {
        super(PATHS.RACE().init);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder home = folder("home");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder nobility = folder("nobility");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder sprite = folder("sprite");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder trait = folder("trait");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder worldcamp = folder("worldcamp");
}
