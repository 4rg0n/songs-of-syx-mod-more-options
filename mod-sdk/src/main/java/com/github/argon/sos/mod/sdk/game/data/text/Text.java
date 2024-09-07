package com.github.argon.sos.mod.sdk.game.data.text;

import com.github.argon.sos.mod.sdk.game.data.GameFolder;
import com.github.argon.sos.mod.sdk.game.data.AbstractGameData;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class Text extends AbstractGameData {
    public Text() {
        super(PATHS.TEXT());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder animal = folder("animal");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder campaign = folder("campaign");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder cutscene = folder("cutscene");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder dictionary = folder("dictionary");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder disease = folder("disease");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder misc = folder("misc");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder names = folder("names");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder player = folder("player");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final TextRace race = new TextRace();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder religion = folder("religion");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final TextResource resource = new TextResource();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder room = folder("room");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final TextSettlement settlement = new TextSettlement();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder stats = folder("stats");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder tech = folder("tech");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder wiki = folder("wiki");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder world = folder("world");
}
