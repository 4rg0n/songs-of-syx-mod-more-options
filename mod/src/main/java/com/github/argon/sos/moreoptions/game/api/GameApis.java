package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

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
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
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
    public void initSettlementUiPresent() {
    }

    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {

    }

    @Override
    public void onCrash(Throwable e) {
    }
}
