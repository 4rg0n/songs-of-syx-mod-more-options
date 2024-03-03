package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import util.save.SaveFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSaveApi implements InitPhases {

    private final static Logger log = Loggers.getLogger(GameSaveApi.class);

    @Getter(lazy = true)
    private final static GameSaveApi instance = new GameSaveApi();

    @Getter
    @Nullable
    private Path currentPath;

    @Getter
    @Nullable
    private SaveFile currentFile;

    private void setCurrent(Path saveFilePath) {
        log.debug("New current save file: %s", saveFilePath);
        SaveFile saveFile = findByPath(saveFilePath).orElse(null);
        this.currentPath = saveFilePath;
        this.currentFile = saveFile;
    }

    public List<SaveFile> getFiles() {
        return Arrays.asList(SaveFile.list());
    }

    @Override
    public void initGameSaved(Path saveFilePath) {
        setCurrent(saveFilePath);
    }

    @Override
    public void initGameSaveLoaded(Path saveFilePath) {
        setCurrent(saveFilePath);
    }

    public Optional<SaveFile> findByPath(Path saveFilePath) {
        return getFiles().stream()
            .filter(saveFile -> saveFilePath.toString().contains(saveFile.fullName))
            .findFirst();
    }

    public Optional<SaveFile> findByName(String saveFileName) {
        return getFiles().stream()
            .filter(saveFile -> saveFile.fullName.equals(saveFileName))
            .findFirst();
    }

    @Nullable
    public String getSaveStamp() {
        if (currentFile == null) {
            return null;
        }

        return currentFile.fullName.toString();
    }
}
