package com.github.argon.sos.mod.sdk.game.asset.text;

import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class TextSettlement extends AbstractGameData {
    public TextSettlement() {
        super(PATHS.SETT().text);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder environment = folder("environment");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder fence = folder("fence");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder floor = folder("floor");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder fortification = folder("fortification");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder structure = folder("structure");
}
