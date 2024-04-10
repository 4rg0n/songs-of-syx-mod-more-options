package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.mapper.JsonMapper;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.writer.JsonWriter;
import com.github.argon.sos.moreoptions.json.writer.JsonWriters;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Manages loading, saving and deleting of json files.
 * Serves as a layer for parsing raw json strings into objects.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonService {
    @Getter(lazy = true)
    private final static JsonService instance = new JsonService(
        JsonWriters.jsonEPretty(),
        FileService.getInstance()
    );
    private final static Logger log = Loggers.getLogger(JsonService.class);

    private final JsonWriter jsonWriter;
    private final FileService fileService;

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        String jsonString = fileService.read(path);

        if (jsonString == null) {
            return Optional.empty();
        }

        try {
            Json json = new Json(jsonString, jsonWriter);
            T object = JsonMapper.mapJson(json.getRoot(), clazz);
            return Optional.of(object);
        }  catch (Exception e) {
            log.warn("Could not parse json from %s for %s", path, clazz.getSimpleName(), e);
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

        try {
            return fileService.write(path, json.write());
        } catch (Exception e) {
            log.warn("Could not write json from object %s into %s",
                path, object.getClass().getSimpleName(), e);
            return false;
        }
    }

    public boolean delete(Path path) {
        return fileService.delete(path);
    }
}
