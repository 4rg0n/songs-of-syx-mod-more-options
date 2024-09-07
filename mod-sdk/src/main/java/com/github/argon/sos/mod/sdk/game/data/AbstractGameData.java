package com.github.argon.sos.mod.sdk.game.data;

import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import init.paths.PATH;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractGameData {
    @Getter
    private final GameFolder folder;

    public AbstractGameData(PATH path) {
        this.folder = GameFolder.of(path);
    }

    public List<Path> paths() {
        return folder.paths();
    }

    public List<Path> filePaths() {
        return folder.filePaths();
    }

    public List<Path> folderPaths() {
        return folder.folderPaths();
    }

    public List<String> folderNames() {
        return folder.folderNames();
    }

    public List<String> fileTitles() {
        return folder.fileTitles();
    }

    public List<String> fileNames() {
        return folder.fileNames();
    }

    public Map<String, GameFolder> folders() {
        return folder.folders();
    }

    public GameFolder folder(String name) {
        return folder.folder(name);
    }

    public Optional<JsonObject> json(String title) {
        return folder.json(title);
    }
}
