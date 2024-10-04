package com.github.argon.sos.mod.sdk.game.asset;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.json.GameJsonService;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a folder in the game data.
 * It gives access to files in the folder.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameFolder {
    private final static Logger log = Loggers.getLogger(GameFolder.class);

    private final GameJsonService gameJsonService = ModSdkModule.gameJsonService();

    private final static Map<PATH, GameFolder> gameFolderStore = new HashMap<>();

    @Getter
    @Accessors(fluent = true, chain = false)
    private final PATH path;

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> paths = Stream.concat(filePaths().stream(), folderPaths().stream())
        .collect(Collectors.toList());

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> filePaths = readFilePaths(path);

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> folderPaths = readFolderPaths(path);

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<String> folderNames = folderPaths().stream()
        .map(Path::getFileName)
        .map(Path::toString)
        .collect(Collectors.toList());

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<String> fileNames = filePaths().stream()
        .map(Path::getFileName)
        .map(Path::toString)
        .sorted()
        .collect(Collectors.toList());

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<String> fileTitles = fileNames().stream()
        .map(StringUtil::removeFileExtension)
        .collect(Collectors.toList());

    private final Map<String, GameFolder> folders = new HashMap<>();

    public GameFolder folder(PATH path) {
        return folder(path.get());
    }

    public GameFolder folder(Path path) {
        if (path.startsWith(this.path.get())) {
            path = this.path.get().relativize(path);
        }

        GameFolder currentFolder = this;
        for (Path element : path) {
            String elementName = element.toString();

            if (isFileName(elementName)) {
                return currentFolder;
            }

            currentFolder = currentFolder.folder(elementName);
        }

        return currentFolder;
    }

    private boolean isFileName(String elementName) {
        return elementName.contains("."); //fishy :x
    }

    public Map<String, GameFolder> folders() {
        return folderNames().stream()
            .collect(Collectors.toMap(name -> name, this::folder));
    }

    public GameFolder folder(String name) {
        if (folders.containsKey(name)) {
            return folders.get(name);
        }

        GameFolder gameFolder = GameFolder.of(path.getFolder(name));

        store(name, gameFolder);
        return gameFolder;
    }

    private void store(String name, GameFolder gameFolder) {
        log.debug("Storing game folder for %s", path.get());
        folders.put(name, gameFolder);
    }

    public Optional<JsonObject> json(String title) {
        if (!path.exists(title)) {
            return Optional.empty();
        }

        return json(path.get(title));
    }

    public static GameFolder of(PATH path) {
        GameFolder gameFolder = gameFolderStore.get(path);

        if (gameFolder != null) {
            return gameFolder;
        }

        gameFolder = new GameFolder(path);
        gameFolderStore.put(path, gameFolder);

        return gameFolder;
    }

    private Optional<JsonObject> json(@Nullable Path filePath) {
        return gameJsonService.get(filePath);
    }

    private static List<Path> readFilePaths(PATH gamePath) {
        Path path;
        try {
            path = gamePath.get();
        } catch (Exception e) {
            log.warn("Game data path does not exist?", e);
            return Lists.of();
        }

        List<Path> paths = new ArrayList<>();

        for (String file : gamePath.getFiles()) {
            paths.add(path.resolve(file));
        }

        return paths;
    }

    private static List<Path> readFolderPaths(PATH gamePath) {
        Path path;
        try {
            path = gamePath.get();
        } catch (Exception e) {
            log.warn("Game data path does not exist?", e);
            return Lists.of();
        }

        List<Path> paths = new ArrayList<>();

        for (String folder : gamePath.folders()) {
            paths.add(path.resolve(folder));
        }

        return paths;
    }
}
