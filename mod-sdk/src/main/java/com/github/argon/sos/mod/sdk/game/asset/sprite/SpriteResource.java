package com.github.argon.sos.mod.sdk.game.asset.sprite;

import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class SpriteResource extends AbstractGameData {
    public SpriteResource() {
        super(PATHS.SPRITE().getFolder("resource"));
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder debris = folder("debris");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder growable = folder("growable");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder minable = folder("minable");
}
