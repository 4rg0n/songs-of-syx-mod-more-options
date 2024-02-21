package com.github.argon.sos.moreoptions.game.api;

public interface InitPhases {
    void initGamePresent();
    void initGameRunning();

    void initBeforeGameCreated();

    void initCreateInstance();
}
