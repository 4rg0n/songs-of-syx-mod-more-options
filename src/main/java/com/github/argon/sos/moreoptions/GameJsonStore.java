package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonWriter;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameJsonStore implements Phases {
    private final static Logger log = Loggers.getLogger(GameJsonStore.class);

    @Getter(lazy = true)
    private final static GameJsonStore instance = new GameJsonStore(
        FileService.getInstance()
    );

    private final FileService fileService;

    private final Map<Path, String> jsonContent = new HashMap<>();

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
    public Json getJson(Path filePath) {
        String content = get(filePath);

        if (content == null) {
            return null;
        }

        try {
            Json json = new Json(content, JsonWriter.jsonE());
            return json;
        } catch (Exception e) {
            log.info("Could not parse json content from %s", filePath, e);
            return null;
        }
    }

    public void put(Path filePath, String content) {
        log.debug("Adding json content for %s", filePath);
        jsonContent.put(filePath, content);
    }

    @Nullable
    public String get(Path filePath) {
        return jsonContent.get(filePath);
    }

    @Nullable
    private String load(Path filePath) {
        String content = fileService.read(filePath);

        if (content != null) {
            put(filePath, content);
        }

        return content;
    }
}
