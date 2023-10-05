package com.github.argon.sos.moreoptions.game.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Contains all game apis for accessing its functionality
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameApis {
    @Getter(lazy = true)
    private final static GameApis instance = new GameApis(
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance(),
        GameUiApi.getInstance(),
        GameWeatherApi.getInstance(),
        GameBoosterApi.getInstance(),
        GameModApi.getInstance()
    );

    @Accessors(fluent = true)
    private final GameEventsApi eventsApi;

    @Accessors(fluent = true)
    private final GameSoundsApi soundsApi;

    @Accessors(fluent = true)
    private final  GameUiApi uiApi;

    @Accessors(fluent = true)
    private final  GameWeatherApi weatherApi;

    @Accessors(fluent = true)
    private final  GameBoosterApi boosterApi;
    @Accessors(fluent = true)
    private final GameModApi modApi;
}
