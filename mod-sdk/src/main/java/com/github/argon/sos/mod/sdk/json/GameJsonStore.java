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
    private final static String TEXT_PATH = "/data/assets/text";
    private final IOService ioService;
    private final Map<Path, Json> jsonObjects = new HashMap<>();
    private final Set<Path> filePaths = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void initBeforeGameCreated() {
        loadRegistered();
    }

    /**
     * Marks a config file to be loaded before a game is crated.
     *
     * @param filePath of config to register
     * @return this instance
     */
    public GameJsonStore register(Path filePath) {
        filePaths.add(filePath);
        return this;
    }

    /**
     * Loads all json config files registered via {@link GameJsonStore#register(Path)}.
     */
    public void loadRegistered() {
        filePaths.forEach(this::readAndStore);
    }

    /**
     * Returns the {@link JsonObject} parsed from the content in the given file path.
     *
     * @param filePath of file with json
     * @return parsed json content
     */
    public Optional<JsonObject> getJsonObject(@Nullable Path filePath) {
        return Optional.ofNullable(getJson(filePath))
            .map(Json::getRoot);
    }

    /**
     * Adds a file with its content to the store.
     * The content will be parsed into a {@link Json} object.
     *
     * @param filePath of the file to add
     * @param content of the file
     */
    public void put(Path filePath, String content) {
        try {
            Json json;
            // json in the text assets use quoted strings
            if (filePath.startsWith(TEXT_PATH)) {
                json = new Json(content, JsonWriters.gameJsonQuotedPretty());
            } else {
                json = new Json(content, JsonWriters.gameJsonUnquotedPretty());
            }

            put(filePath, json);
        } catch (JsonParseException e) {
            log.error("Could not add json %s to store", filePath, e);
        }
    }

    /**
     * Adds a file path with its {@link Json} content into the store.
     *
     * @param filePath of the file to add
     * @param json of store
     */
    public void put(Path filePath, Json json) {
        log.debug("Adding json for %s", filePath);
        jsonObjects.put(filePath, json);
    }

    /**
     * Returns the stored {@link Json} for given file path.
     * If there's no entry for the path, it will try to read, parse and store the file content from the path.
     *
     * @param filePath of the file to get
     * @return parsed json or null if the file doesn't exist
     */
    @Nullable
    public Json getJson(@Nullable Path filePath) {
        if (filePath == null) {
            return null;
        }

        Json json = jsonObjects.get(filePath);
        if (json == null) {
            String content = readAndStore(filePath);

            if (content == null) {
                return null;
            }
        }

        return jsonObjects.get(filePath);
    }

    /**
     * Returns the content as a {@link String} for given file path.
     *
     * @param filePath to get content from
     * @return json content of file or null if the file does not exist
     */
    @Nullable
    public String getContent(Path filePath) {
        Json json = jsonObjects.get(filePath);

        if (json == null) {
            return null;
        }

        return json.write();
    }

    @Nullable
    private String readAndStore(Path filePath) {
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
