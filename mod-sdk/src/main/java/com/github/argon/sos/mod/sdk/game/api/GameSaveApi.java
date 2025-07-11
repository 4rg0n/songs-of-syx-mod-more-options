package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.util.SaveUtil;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import game.save.SaveFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * For interacting with the games save files and features
 */
@RequiredArgsConstructor
public class GameSaveApi implements Phases {
    private final static Logger log = Loggers.getLogger(GameSaveApi.class);

    /**
     * Path to the current game save file
     */
    @Getter
    @Nullable
    private Path currentPath;

    /**
     * Currently used game save file
     */
    @Nullable
    private SaveFile currentFile;

    public @Nullable SaveFile getCurrentFile() {
        if (currentPath == null) {
            return null;
        }

        if (currentFile == null) {
            currentFile = findByPathContains(currentPath).orElse(null);
        }

        return currentFile;
    }

    private void setCurrent(Path saveFilePath) {
        SaveFile saveFile = findByPathContains(saveFilePath).orElse(null);
        log.debug("Set current saveFilePath: %s", saveFilePath);

        this.currentPath = saveFilePath;
        this.currentFile = saveFile;
    }

    /**
     * @return all available game save files
     */
    public List<SaveFile> getFiles() {
        return Arrays.asList(SaveFile.list());
    }

    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {
        // update save info on game save
        setCurrent(saveFilePath);
    }

    @Override
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
        // update save info on game loaded
        setCurrent(saveFilePath);
    }

    /**
     * Find a {@link SaveFile} by a path or a part of it.
     * @return first matching
     */
    public Optional<SaveFile> findByPathContains(Path saveFilePath) {
        return getFiles().stream()
            .filter(saveFile -> saveFilePath.toString().contains(saveFile.fullName))
            .findFirst();
    }

    /**
     * Finds a {@link SaveFile} by exact save file name
     *
     * @return first matching
     */
    public Optional<SaveFile> findByName(String saveFileName) {
        return getFiles().stream()
            .filter(saveFile -> saveFile.fullName.equals(saveFileName))
            .findFirst();
    }

    /**
     * @return unique save file identifier
     */
    @Nullable
    public String getSaveStamp() {
        if (currentFile == null) {
            if (currentPath != null) {
                return SaveUtil.extractSaveStamp(currentPath);
            }

            return null;
        }

        return SaveUtil.extractSaveStamp(currentFile);
    }
}
