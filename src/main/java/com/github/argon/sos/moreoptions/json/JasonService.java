package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.writer.JsonWriter;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses custom JSON implementation
 *
 * Manages loading, saving and deleting of json files.
 * Serves as a layer for parsing raw json strings into objects.
 */
@RequiredArgsConstructor
public class JasonService implements JsonService {
    @Getter(lazy = true)
    private final static JasonService instance = new JasonService(
        JsonWriters.jsonEPretty(), // will write game json as default
        FileService.getInstance()
    );
    private final static Logger log = Loggers.getLogger(JasonService.class);

    private final JsonWriter jsonWriter;
    private final FileService fileService;

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        return load(path)
            .map(json -> {
                try {
                    return JsonMapper.mapJson(json.getRoot(), clazz);
                }  catch (Exception e) {
                    log.warn("Could not map json from %s for %s", path, clazz.getSimpleName(), e);
                    return null;
                }
            });
    }

    public Optional<Json> load(Path path) {
        String jsonString = fileService.read(path);

        if (jsonString == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new Json(jsonString, jsonWriter));
        }  catch (Exception e) {
            log.warn("Could not load json from %s", path, e);
            return Optional.empty();
        }
    }

    public boolean save(Path path, Object object) {
        Json json;
        try {
            JsonElement jsonElement = JsonMapper.mapObject(object);
            json = new Json(jsonElement, jsonWriter);
        } catch (Exception e) {
            log.warn("Could not create json from object %s for saving in %s",
                path, object.getClass().getSimpleName(), e);
            return false;
        }

        boolean success = save(path, json);
        if (!success) {
            log.warn("Could not write json from object %s", object.getClass().getSimpleName());
        }

        return success;
    }

    public boolean save(Path path, Json json) {
        try {
            return fileService.write(path, json.write());
        } catch (Exception e) {
            log.warn("Could not write json to into %s", path, e);
            return false;
        }
    }

    public boolean delete(Path path) {
        return fileService.delete(path);
    }
}
