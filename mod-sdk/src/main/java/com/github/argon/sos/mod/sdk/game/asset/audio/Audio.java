package com.github.argon.sos.mod.sdk.game.asset.audio;

import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Represents the game "audio" folder structure.
 */
public class Audio extends AbstractGameData {
    /**
     * Creates a new "audio" folder structure instance
     */
    public Audio() {
        super(PATHS.AUDIO().config);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder mono = folder("mono");

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder ambience = folder("ambience");
}
