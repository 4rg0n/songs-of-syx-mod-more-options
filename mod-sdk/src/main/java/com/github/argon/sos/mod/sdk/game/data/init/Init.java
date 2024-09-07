package com.github.argon.sos.mod.sdk.game.data.init;

import com.github.argon.sos.mod.sdk.game.data.GameFolder;
import com.github.argon.sos.mod.sdk.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class Init extends AbstractGameData {
    public Init() {
        super(PATHS.INIT());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder animal = folder("animal");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder battle = folder("battle");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder config = folder("config");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder disease = folder("disease");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder player = folder("player");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final InitRace race = new InitRace();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder religion = folder("religion");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final InitResource resource = new InitResource();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder room = folder("room");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final InitSettlement settlement = new InitSettlement();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder stats = folder("stats");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder tech = folder("tech");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder world = folder("world");
}
