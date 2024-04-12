package com.github.argon.sos.moreoptions.game.data;

import com.github.argon.sos.moreoptions.game.json.GameJsonService;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.StringUtil;
import init.paths.PATH;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class GameFolder {
    private final static Logger log = Loggers.getLogger(GameFolder.class);

    private final GameJsonService gameJsonService = GameJsonService.getInstance();

    @Getter
    @Accessors(fluent = true, chain = false)
    private final PATH path;

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> paths = readPaths(path);

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> filePaths = paths().stream()
        .filter(filePath -> !Files.isDirectory(filePath))
        .collect(Collectors.toList());

    @Getter(lazy = true)
    @Accessors(fluent = true, chain = false)
    private final List<Path> folderPaths = paths().stream()
        .filter(Files::isDirectory)
        .collect(Collectors.toList());

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
        return new GameFolder(path);
    }

    private Optional<JsonObject> json(@Nullable Path filePath) {
        return gameJsonService.get(filePath);
    }

    private static List<Path> readPaths(PATH path) {
        try {
            path.get();
        } catch (Exception e) {
            log.warn("Game data path does not exist?", e);
            return Lists.of();
        }

        try (Stream<Path> stream = Files.list(path.get())) {
            return stream.collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Could not read game data files from %s", path.get());
            return Lists.of();
        }
    }
}
