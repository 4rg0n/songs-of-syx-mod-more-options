package com.github.argon.sos.moreoptions.game.json;

import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameJsonService {
    private final static Logger log = Loggers.getLogger(GameJsonService.class);

    @Getter(lazy = true)
    private final static GameJsonService instance = new GameJsonService(
        GameJsonStore.getInstance()
    );

    private final GameJsonStore gameJsonStore;

    public List<GameJsonResource> get(PATH filePath) {
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

    public Optional<GameJsonResource> get(Path filePath) {
        return gameJsonStore.getJsonObject(filePath)
            .map(jsonObject -> GameJsonResource.builder()
                .json(jsonObject)
                .path(filePath)
                .build());
    }

    @Getter
    @Builder
    public static class GameJsonResource {
        private final Path path;
        private final JsonObject json;
    }
}
