package com.github.argon.sos.moreoptions.game.data.text;

import com.github.argon.sos.moreoptions.game.data.GameFolder;
import com.github.argon.sos.moreoptions.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class TextRace extends AbstractGameData {
    public TextRace() {
        super(PATHS.RACE().text);
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder bio = folder("bio");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder king = folder("king");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder nobility = folder("nobility");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder opinion = folder("opinion");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder raid = folder("raid");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder tourist = folder("tourist");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder trait = folder("trait");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder worldcamp = folder("worldcamp");
}
