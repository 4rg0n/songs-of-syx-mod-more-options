package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gives access to the game JSON configuration
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameJsonService {
    private final static Logger log = Loggers.getLogger(GameJsonService.class);

    @Getter(lazy = true)
    private final static GameJsonService instance = new GameJsonService(
        GameJsonStore.getInstance()
    );

    private final GameJsonStore gameJsonStore;

    /**
     * @param filePath game path of the json config to read
     * @return parsed {@link JsonObject} when file was readable
     */
    public List<JsonObject> get(PATH filePath) {
        try (Stream<Path> stream = Files.list(filePath.get())) {
            return stream
                .filter(file -> !Files.isDirectory(file))
                .filter(path1 -> path1.toString().endsWith(".txt"))
                .map(path -> get(path).orElse(null))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.debug("Could not read jsons", e);
            return Lists.of();
        }
    }

    /**
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
