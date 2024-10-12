package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.util.SaveUtil;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.save.SaveFile;

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
    @Getter
    @Nullable
    private SaveFile currentFile;
    @Nullable
    public FileGetter fileGetter;
    @Nullable
    public FilePutter filePutter;

    private void setCurrent(Path saveFilePath) {
        SaveFile saveFile = findByPathContains(saveFilePath).orElse(null);
        if (log.isLevel(Level.DEBUG)) {
            log.debug("Set current saveFilePath: %s", saveFilePath);
        }

        this.currentPath = saveFilePath;
        this.currentFile = saveFile;
    }

    public void setCurrent(FileGetter fileGetter){
        this.fileGetter = fileGetter;
    }

    public void setCurrent(FilePutter filePutter){
        this.filePutter = filePutter;
    }

    /**
     * @return all available game save files
     */
    public List<SaveFile> getFiles() {
        return Arrays.asList(SaveFile.list());
    }

    @Override
    public void onGameSaved(Path saveFilePath, IFileSave fileSaver) {
        // update save info on game save
        setCurrent(saveFilePath);
    }

    @Override
    public void onGameLoaded(Path saveFilePath, IFileLoad fileLoader) {
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
            return null;
        }

        return SaveUtil.extractSaveStamp(currentFile);
    }
}
