package com.github.argon.sos.mod.sdk.game.asset.text;

import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class TextResource extends AbstractGameData {
    public TextResource() {
        super(PATHS.TEXT().getFolder("resource"));
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
