package com.github.argon.sos.moreoptions.game.json;

import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.parser.JsonParseException;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Can hold the game JSON configuration
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameJsonStore implements Phases {
    private final static Logger log = Loggers.getLogger(GameJsonStore.class);

    @Getter(lazy = true)
    private final static GameJsonStore instance = new GameJsonStore(
        FileService.getInstance()
    );

    private final FileService fileService;

    private final Map<Path, String> jsonContent = new HashMap<>();
    private final Map<Path, Json> jsonObjects = new HashMap<>();
    private final Set<Path> filePaths = new HashSet<>();

    @Override
    public void initBeforeGameCreated() {
        loadRegistered();
    }

    public GameJsonStore register(Path filePath) {
        filePaths.add(filePath);
        return this;
    }

    public void loadRegistered() {
        filePaths.forEach(this::load);
    }

    @Nullable
    public snake2d.util.file.Json getJsonE(Path filePath) {
        String content = getContent(filePath);

        if (content == null) {
            content = load(filePath);

            if (content == null) {
                return null;
            }
        }

        try {
            snake2d.util.file.Json json = new snake2d.util.file.Json(content, filePath.toString(), false);
            return json;
        } catch (Exception e) {
            log.info("Could not parse jsonE content from %s", filePath, e);
            return null;
        }
    }

    public Optional<JsonObject> getJsonObject(@Nullable Path filePath) {
        return Optional.ofNullable(getJson(filePath))
            .map(Json::getRoot);
    }

    @Nullable
    public Json getJson(@Nullable Path filePath) {
        if (filePath == null) {
            return null;
        }

        String content = getContent(filePath);

        if (content == null) {
            content = load(filePath);

            if (content == null) {
                return null;
            }
        }

        try {
            return jsonObjects.get(filePath);
        } catch (Exception e) {
            log.info("Could not parse json content from %s", filePath, e);
            return null;
        }
    }

    public void put(Path filePath, String content) {
        log.debug("Adding json content for %s", filePath);
        jsonContent.put(filePath, content);
        try {
            jsonObjects.put(filePath, new Json(content, JsonWriters.jsonEPretty()));
        } catch (JsonParseException e) {
            log.error("Could not add json %s to store", filePath, e);
        }
    }

    @Nullable
    public String getContent(Path filePath) {
        return jsonContent.get(filePath);
    }

    @Nullable
    private String load(Path filePath) {
        String content = null;
        try {
            content = fileService.read(filePath);
        } catch (IOException e) {
            // ignore
        }

        if (content != null) {
            put(filePath, content);
        }

        return content;
    }
}
