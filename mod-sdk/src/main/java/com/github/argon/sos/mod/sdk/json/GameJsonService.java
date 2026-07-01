package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import init.paths.PATH;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Gives access to the game JSON configuration from the data.zip file
 */
@RequiredArgsConstructor
public class GameJsonService {
    private final static Logger log = Loggers.getLogger(GameJsonService.class);
    private final GameJsonStore gameJsonStore;

    /**
     * Reads and parses all game config files in a given folder.
     *
     * @param folderPath game folder path of the json configs to read
     * @return parsed {@link JsonObject} when file was readable
     */
    public List<JsonObject> get(PATH folderPath) {
        try (Stream<Path> stream = Files.list(folderPath.get())) {
            return stream
                .filter(file -> !Files.isDirectory(file))
                .filter(path1 -> path1.toString().endsWith(".txt"))
                .map(path -> get(path).orElse(null))
                .toList();
        } catch (Exception e) {
            log.debug("Could not read jsons", e);
            return List.of();
        }
    }

    /**
     * Reads and parses a config file.
     *
     * @param filePath of the json config to read
     * @return parsed {@link JsonObject} when file was readable
     */
    public Optional<JsonObject> get(@Nullable Path filePath) {
        if (filePath == null) {
            return Optional.empty();
        }

        return gameJsonStore.getJsonObject(filePath);
    }
}
