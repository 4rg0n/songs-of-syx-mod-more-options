package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.ModModule;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameApiModule {
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameStatsApi stats = buildGameStatsApi();
    private static GameStatsApi buildGameStatsApi() {
        return new GameStatsApi(ModModule.metricCollector());
    }

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static GameBoosterApi boosters = buildGameBoosterApi();
    private static GameBoosterApi buildGameBoosterApi() {
        return new GameBoosterApi(ModModule.booster());
    }
}
