package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;

/**
 * For accessing vanilla game classes and features.
 * Contains all game apis for accessing its functionality.
 */
@Getter
@RequiredArgsConstructor
public class GameApis implements Phases, Resettable {

    @Accessors(fluent = true)
    private final GameEventsApi events;

    @Accessors(fluent = true)
    private final GameSoundsApi sounds;

    @Accessors(fluent = true)
    private final GameUiApi ui;

    @Accessors(fluent = true)
    private final GameWeatherApi weather;

    @Accessors(fluent = true)
    private final GameModApi mod;

    @Accessors(fluent = true)
    private final GameRaceApi race;

    @Accessors(fluent = true)
    private final GameSaveApi save;

    @Accessors(fluent = true)
    private final GameLangApi lang;

    @Accessors(fluent = true)
    private final GameFactionApi faction;

    @Accessors(fluent = true)
    private final GameRoomsApi rooms;

    @Accessors(fluent = true)
    private final GameStatsApi stats;

    @Accessors(fluent = true)
    private final GameAnimalsApi animals;

    @Override
    public void reset() {
        // game will initialize new instances of the cached class references on load
        events().reset();
        sounds().reset();
        weather().reset();
        faction().reset();
    }

    @Override
    public void initGameUiPresent() {}

    @Override
    public void initGameUpdating() {
        ui().initGameUpdating();
    }

    @Override
    public void initBeforeGameCreated() {}

    @Override
    public void initModCreateInstance() {
        race().initModCreateInstance();
    }

    @Override
    public void initNewGameSession() {
    }

    @Override
    public void initSettlementUiPresent() {
        faction.initSettlementUiPresent();
    }

    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {
        save().onGameSaved(saveFilePath, filePutter);
    }

    @Override
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
        save().onGameLoaded(saveFilePath, fileGetter);
    }

    @Override
    public void onGameSaveReloaded() {
        reset();
    }

    @Override
    public void onGameUpdate(double seconds) {
    }

    @Override
    public void onCrash(Throwable e) {
    }
}
