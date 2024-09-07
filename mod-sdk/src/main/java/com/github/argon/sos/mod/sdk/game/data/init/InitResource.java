package com.github.argon.sos.mod.sdk.game.data.init;

import com.github.argon.sos.mod.sdk.game.data.GameFolder;
import com.github.argon.sos.mod.sdk.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class InitResource extends AbstractGameData {
    public InitResource() {
        super(PATHS.INIT().getFolder("resource"));
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder edible = folder("edible");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder drinkable = folder("drinkable");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder growable = folder("growable");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder minable = folder("minable");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder armySupply = folder("armySupply");
}
