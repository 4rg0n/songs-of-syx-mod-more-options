package com.github.argon.sos.mod.sdk.game.asset.sprite;

import com.github.argon.sos.mod.sdk.game.asset.AbstractGameData;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import init.paths.PATHS;
import lombok.Getter;
import lombok.experimental.Accessors;

public class Sprite extends AbstractGameData {
    public Sprite() {
        super(PATHS.SPRITE());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder animal = folder("animal");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final SpriteRace race = new SpriteRace();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final SpriteResource resource = new SpriteResource();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder room = folder("room");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final SpriteSettlement settlement = new SpriteSettlement();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder world = folder("world");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder font = folder("font");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder game = folder("game");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder icon = folder("icon");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder image = folder("image");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder menu = folder("menu");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder textures = folder("textures");
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final GameFolder ui = folder("ui");
}
