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
     * The game uses a single save file when a battle starts.
     */
    public final static String BEFORE_BATTLE_SAVE = "__beforeBattle.save";

    /**
     * The game uses a single save file when in a battle.
     */
    public final static String BATTLE_SAVE = "__battle.save";

    /**
     * When starting a new settlement, the game will create a save file with this as prefix.
     */
    public final static String NEW_GAME_SAVE_PREFIX = "A new Beginning";

    /**
     * The game will save periodically and will use prefix for it.
     */
    public final static String AUTO_SAVE_PREFIX = "AutoSave";

    /**
     * Current game battle save {@link Path} or null when not in battle.
     */
    @Getter
    @Nullable
    private Path battleSavePath;

    /**
     * Current game battle {@link SaveFile} or null when not in battle.
     */
    @Getter
    @Nullable
    private SaveFile battleSaveFile;

    /**
     * Current game before battle save {@link Path} or null when not in battle.
     */
    @Getter
    @Nullable
    private Path beforeBattleSavePath;

    /**
     * Current game before battle {@link SaveFile} or null when not in battle.
     */
    @Getter
    @Nullable
    private SaveFile beforeBattleSaveFile;

    /**
     * Current game save {@link Path} or null when no save exists yet.
     */
    @Getter
    @Nullable
    private Path savePath;

    /**
     * Currently used game {@link SaveFile} or null when no save exists yet.
     */
    @Nullable
    private SaveFile saveFile;

    /**
     * Checks whether given save path is a battle save.
     *
     * @param savePath to check
     * @return whether given save path is a battle save
     */
    public boolean isBattleSave(Path savePath) {
        return savePath.endsWith(BATTLE_SAVE);
    }

    /**
     * Checks whether given save path is a before battle save.
     *
     * @param savePath to check
     * @return whether given save path is a before battle save
     */
    public boolean isBeforeBattleSave(Path savePath) {
        return savePath.endsWith(BEFORE_BATTLE_SAVE);
    }

    /**
     * Checks whether given save path is a new game save.
     *
     * @param savePath to check
     * @return whether given save path is a new game save
     */
    public boolean isNewGameSave(Path savePath) {
        return savePath.getFileName().startsWith(NEW_GAME_SAVE_PREFIX);
    }

    /**
     * Checks whether given save path is an auto save.
     *
     * @param savePath to check
     * @return whether given save path is an auto save
     */
    public boolean isAutoSave(Path savePath) {
        return savePath.getFileName().startsWith(AUTO_SAVE_PREFIX);
    }

    /**
     * Returns the current latest {@link SaveFile} for the game session.
     *
     * @return current save file or null when not saved yet
     */
    @Nullable
    public SaveFile getSaveFile() {
        if (savePath == null) {
            return null;
        }

        if (saveFile == null) {
            saveFile = findByPathContains(savePath).orElse(null);
        }

        return saveFile;
    }

    /**
     * Returns a list of all save files for the game session.
     *
     * @return all available game save files
     */
    public List<SaveFile> getFiles() {
        return Arrays.asList(SaveFile.list());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {
        // update save info on game save
        setCurrent(saveFilePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
        // update save info on game loaded
        setCurrent(saveFilePath);
    }

    /**
     * Find a {@link SaveFile} by a path or a part of it.
     *
     * @param saveFilePath to find
     * @return first matching save file
     */
    public Optional<SaveFile> findByPathContains(Path saveFilePath) {
        return getFiles().stream()
            .filter(saveFile -> saveFilePath.toString().contains(saveFile.fullName))
            .findFirst();
    }

    /**
     * Finds a {@link SaveFile} by exact save file name.
     *
     * @param saveFileName to look for
     * @return first matching save file
     */
    public Optional<SaveFile> findByName(String saveFileName) {
        return getFiles().stream()
            .filter(saveFile -> saveFile.fullName.equals(saveFileName))
            .findFirst();
    }

    /**
     * Returns the unique identifier of save files for the current game session.
     *
     * @return unique save file identifier
     */
    @Nullable
    public String getSaveStamp() {
        if (saveFile == null) {
            if (savePath != null) {
                return SaveUtil.extractSaveStamp(savePath);
            }

            return null;
        }

        return SaveUtil.extractSaveStamp(saveFile);
    }

    private void setCurrent(Path saveFilePath) {
        if (isBeforeBattleSave(saveFilePath)) {
            setCurrentBeforeBattle(saveFilePath);
        } else if (isBattleSave(saveFilePath)) {
            setCurrentBattle(saveFilePath);
        } else {
            setCurrentSave(saveFilePath);
        }
    }

    private void setCurrentSave(Path saveFilePath) {
        SaveFile saveFile = findByPathContains(saveFilePath).orElse(null);
        log.debug("Set current saveFilePath: %s", saveFilePath);

        this.savePath = saveFilePath;
        this.saveFile = saveFile;
        // reset the battle saves, because we aren't in a battle anymore
        this.battleSavePath = null;
        this.battleSaveFile = null;
        this.beforeBattleSavePath = null;
        this.beforeBattleSaveFile = null;
    }

    private void setCurrentBeforeBattle(Path battleSavePath) {
        SaveFile battleSaveFile = findByPathContains(battleSavePath).orElse(null);
        log.debug("Set current beforeBattleSavePath: %s", battleSavePath);

        this.beforeBattleSavePath = battleSavePath;
        this.beforeBattleSaveFile = battleSaveFile;
    }

    private void setCurrentBattle(Path battleSavePath) {
        SaveFile battleSaveFile = findByPathContains(battleSavePath).orElse(null);
        log.debug("Set current battleSavePath: %s", battleSavePath);

        this.battleSavePath = battleSavePath;
        this.battleSaveFile = battleSaveFile;
    }
}
