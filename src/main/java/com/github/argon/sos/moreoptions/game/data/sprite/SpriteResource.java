package com.github.argon.sos.moreoptions.game.data.sprite;

import com.github.argon.sos.moreoptions.game.data.AbstractGameData;
import com.github.argon.sos.moreoptions.game.data.GameFolder;
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
