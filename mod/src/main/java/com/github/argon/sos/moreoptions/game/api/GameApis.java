package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.mod.sdk.game.api.IFileLoad;
import com.github.argon.sos.mod.sdk.game.api.IFileSave;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class GameApis implements Phases {

    @Accessors(fluent = true)
    private final GameBoosterApi boosters;

    @Override
    public void initBeforeGameCreated() {
    }

    @Override
    public void initModCreateInstance() {
        boosters().initModCreateInstance();
    }

    @Override
    public void onGameLoaded(Path saveFilePath, IFileLoad fileLoader) {
    }

    @Override
    public void onGameSaveReloaded() {
        boosters().onGameSaveReloaded();
    }

    @Override
    public void initNewGameSession() {
    }

    @Override
    public void initGameUpdating() {
    }

    @Override
    public void onGameUpdate(double seconds) {
    }

    @Override
    public void initGameUiPresent() {
    }

    @Override
    public void onViewSetup() {
    }

    @Override
    public void initSettlementUiPresent() {
    }

    @Override
    public void onGameSaved(Path saveFilePath, IFileSave fileSaver) {

    }

    @Override
    public void onCrash(Throwable e) {
    }
}
