package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.init.InitPhases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

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
        GameSaveApi.getInstance()
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

    @Override
    public void initGamePresent() {}

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
    public void initGameSaveLoaded(MoreOptionsV2Config config) {
        save().initGameSaveLoaded(config);
    }
}
