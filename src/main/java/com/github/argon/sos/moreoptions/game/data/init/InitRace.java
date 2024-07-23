package com.github.argon.sos.moreoptions.game.data.init;

import com.github.argon.sos.moreoptions.game.data.GameFolder;
import com.github.argon.sos.moreoptions.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class InitRace extends AbstractGameData {
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
