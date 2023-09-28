package com.github.argon.sos.moreoptions.game.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameApis {
    @Getter(lazy = true)
    private final static GameApis instance = new GameApis(
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance(),
        GameUiApi.getInstance(),
        GameWeatherApi.getInstance(),
        GameBoosterApi.getInstance()
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
}
