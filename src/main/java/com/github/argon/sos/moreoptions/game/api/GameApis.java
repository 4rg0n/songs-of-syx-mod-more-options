package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.init.InitPhases;
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
public class GameApis implements InitPhases {
    @Getter(lazy = true)
    private final static GameApis instance = new GameApis(
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance(),
        GameUiApi.getInstance(),
        GameWeatherApi.getInstance(),
        GameBoosterApi.getInstance(),
        GameModApi.getInstance(),
        GameStatsApi.getInstance(),
        GameRaceApi.getInstance(),
        GameSaveApi.getInstance(),
        GameLangApi.getInstance()
    );

    @Accessors(fluent = true)
    private final GameEventsApi events;

    @Accessors(fluent = true)
    private final GameSoundsApi sounds;

    @Accessors(fluent = true)
    private final  GameUiApi ui;

    @Accessors(fluent = true)
    private final  GameWeatherApi weather;

    @Accessors(fluent = true)
    private final  GameBoosterApi booster;

    @Accessors(fluent = true)
    private final GameModApi mod;

    @Accessors(fluent = true)
    private final GameStatsApi stats;

    @Accessors(fluent = true)
    private final GameRaceApi race;

    @Accessors(fluent = true)
    private final GameSaveApi save;

    @Accessors(fluent = true)
    private final GameLangApi lang;

    public void clear() {
        // game will initialize new instances of the cached class references on load
        events().clearCached();
        sounds().clearCached();
        weather().clearCached();
        booster().clearCached();
    }

    @Override
    public void initGameUiPresent() {}

    @Override
    public void initGameRunning() {
        ui().initGameRunning();
    }

    @Override
    public void initBeforeGameCreated() {

    }

    @Override
    public void initCreateInstance() {
        booster().initCreateInstance();
        race().initCreateInstance();
    }

    @Override
    public void initNewGameSession() {
    }

    @Override
    public void initGameSaved(Path saveFilePath) {
        save().initGameSaved(saveFilePath);
    }

    @Override
    public void initGameSaveLoaded(Path saveFilePath) {
        save().initGameSaveLoaded(saveFilePath);
    }

    @Override
    public void initGameSaveReloaded() {
    }
}
