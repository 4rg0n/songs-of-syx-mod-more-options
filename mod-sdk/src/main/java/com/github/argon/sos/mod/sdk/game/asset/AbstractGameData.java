package com.github.argon.sos.mod.sdk.game.asset;

import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import init.paths.PATH;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provides basic methods to deal with the games assets.
 */
public abstract class AbstractGameData {
    @Getter
    private final GameFolder folder;

    /**
     * Creates game data from a given folder.
     *
     * @param folderPath of the game asset folder
     */
    public AbstractGameData(PATH folderPath) {
        this.folder = GameFolder.of(folderPath);
    }

    /**
     * Lists all file and folder paths in this game asset folder.
     *
     * @return all file and folder paths in this folder
     */
    public List<Path> paths() {
        return folder.paths();
    }

    /**
     * Lists all file paths in this game asset folder.
     *
     * @return list of only file paths in this folder
     */
    public List<Path> filePaths() {
        return folder.filePaths();
    }

    /**
     * Lists all folder paths in this game asset folder.
     *
     * @return list of only folder paths in this folder
     */
    public List<Path> folderPaths() {
        return folder.folderPaths();
    }

    /**
     * Lists all folder names in this game asset folder.
     *
     * @return list of only folder names in this folder
     */
    public List<String> folderNames() {
        return folder.folderNames();
    }

    /**
     * Lists all file names without the file extension in this game asset folder.
     *
     * @return list of only file names without file extension in this folder
     */
    public List<String> fileTitles() {
        return folder.fileTitles();
    }

    /**
     * Lists all file names with the file extension in this game asset folder.
     *
     * @return list of only file names with file extension in this folder
     */
    public List<String> fileNames() {
        return folder.fileNames();
    }

    /**
     * Creates a map of all folder names with their {@link GameFolder} in this game asset folder.
     *
     * @return map of all folder names with their {@link GameFolder} in this game asset folder.
     */
    public Map<String, GameFolder> folders() {
        return folder.folders();
    }

    /**
     * Will create a {@link GameFolder} with given name.
     * It will not create a real folder in the filesystem.
     *
     * @param name folder name
     * @return the created game folder
     */
    public GameFolder folder(String name) {
        return folder.folder(name);
    }

    /**
     * Will read the content of a game config file as {@link JsonObject} if possible.
     *
     * @param title of the file to read
     * @return read json
     */
    public Optional<JsonObject> json(String title) {
        return folder.json(title);
    }
}
