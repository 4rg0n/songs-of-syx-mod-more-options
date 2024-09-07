package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.file.Path;

/**
 * For accessing vanilla game classes and features.
 * Contains all game apis for accessing its functionality.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameApis implements Phases {
    @Getter(lazy = true)
    private final static GameApis instance = new GameApis(
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance(),
        GameUiApi.getInstance(),
        GameWeatherApi.getInstance(),
        GameModApi.getInstance(),
        GameRaceApi.getInstance(),
        GameSaveApi.getInstance(),
        GameLangApi.getInstance(),
        GameFactionApi.getInstance(),
        GameRoomsApi.getInstance()
    );

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

    public void clear() {
        // todo need to verify again...
        // game will initialize new instances of the cached class references on load
        events().clearCached();
        sounds().clearCached();
        weather().clearCached();
        faction().clearCached();
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
    public void onViewSetup() {
        faction.onViewSetup();
    }

    @Override
    public void initSettlementUiPresent() {

    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        save().onGameSaved(saveFilePath);
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        save().onGameSaveLoaded(saveFilePath);
    }

    @Override
    public void onGameSaveReloaded() {
        clear();
    }

    @Override
    public void onGameUpdate(double seconds) {
    }

    @Override
    public void onCrash(Throwable e) {
    }
}