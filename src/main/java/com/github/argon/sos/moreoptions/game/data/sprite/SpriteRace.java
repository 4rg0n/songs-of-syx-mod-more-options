package com.github.argon.sos.moreoptions.game.data.sprite;

import com.github.argon.sos.moreoptions.game.data.GameFolder;
import com.github.argon.sos.moreoptions.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class SpriteRace extends AbstractGameData {
    public SpriteRace() {
        super(PATHS.RACE().sprite);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder child = folder("child");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder extra = folder("extra");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder face = folder("face");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder misc = folder("misc");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder portrait = folder("portrait");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder skelleton = folder("skelleton");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder sleep = folder("sleep");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder worldcamp = folder("worldcamp");
}
