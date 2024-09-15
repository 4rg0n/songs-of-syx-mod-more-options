package com.github.argon.sos.mod.sdk.game.asset.sprite;

import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class SpriteSettlement extends AbstractGameData {
    public SpriteSettlement() {
        super(PATHS.SETT().sprite);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder floor = folder("floor");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder fortification = folder("fortification");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder map = folder("map");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder projectile = folder("projectile");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder structure = folder("structure");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder thing = folder("thing");
}
