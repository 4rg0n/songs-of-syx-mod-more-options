package com.github.argon.sos.moreoptions.game.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
public class GameApis {

    @Accessors(fluent = true)
    private final GameBoosterApi boosters;
}
