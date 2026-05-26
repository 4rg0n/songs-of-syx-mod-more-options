package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.json.parser.JsonParseException;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Can cache the game JSON configuration by file path.
 * <p>
 * MODDED: keys are normalized {@link Path#toString()} (with {@code \} → {@code /}) so that
 * lookups succeed across different {@link java.nio.file.spi.FileSystemProvider} instances
 * (Windows filesystem vs zip), which is required in v71 where mods overlay the base data.zip.
 */
@RequiredArgsConstructor
public class GameJsonStore implements Phases {
    private final static Logger log = Loggers.getLogger(GameJsonStore.class);

    private final IOService ioService;

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

    public Optional<JsonObject> getJsonObject(@Nullable Path filePath) {
        return Optional.ofNullable(getJson(filePath))
            .map(Json::getRoot);
    }

    public void put(Path filePath, String content) {
        try {
            Json json = new Json(content, JsonWriters.jsonEPretty());
            put(filePath, json);
        } catch (JsonParseException e) {
            log.error("Could not add json %s to store", filePath, e);
        }
    }

    public void put(Path filePath, Json json) {
        log.debug("Adding json for %s", filePath);
        jsonObjects.put(filePath, json);
    }

    @Nullable
    public Json getJson(@Nullable Path filePath) {
        if (filePath == null) {
            return null;
        }

        Json json = jsonObjects.get(filePath);
        if (json == null) {
            String content = load(filePath);

            if (content == null) {
                return null;
            }
        }

        return jsonObjects.get(filePath);
    }

    @Nullable
    public String getContent(Path filePath) {
        Json json = jsonObjects.get(filePath);

        if (json == null) {
            return null;
        }

        return json.write();
    }

    @Nullable
    private String load(Path filePath) {
        String content = null;
        try {
            content = ioService.read(filePath);
        } catch (IOException e) {
            // ignore
        }

        if (content != null) {
            put(filePath, content);
        }

        return content;
    }
}
