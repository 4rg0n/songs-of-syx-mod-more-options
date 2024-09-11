package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses custom game JSON implementation
 *
 * Manages loading, saving and deleting of json files.
 * Serves as a layer for parsing raw json strings into objects.
 */
@RequiredArgsConstructor
public class JsonGameService implements JsonService {

    private final JsonWriter jsonWriter;
    private final IOService ioService;

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        return load(path)
            .map(json -> {
                try {
                    return JsonMapper.mapJson(json.getRoot(), clazz);
                }  catch (Exception e) {
                    throw new JsonException(String.format("Could not map json from %s for %s", path, clazz.getSimpleName()), e);
                }
            });
    }

    public Optional<Json> load(Path path) {
        String jsonString;
        try {
            jsonString = ioService.read(path);
        } catch (IOException e) {
            throw new JsonException(String.format("Could not rad json file %s", path), e);
        }

        if (jsonString == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new Json(jsonString, jsonWriter));
        }  catch (Exception e) {
            throw new JsonException(String.format("Could not load json from %s", path), e);
        }
    }

    public void save(Path path, Object object) {
        Json json;
        try {
            JsonElement jsonElement = JsonMapper.mapObject(object);
            json = new Json(jsonElement, jsonWriter);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not create json from object %s for saving in %s",
                path, object.getClass().getSimpleName()), e);
        }

        save(path, json);
    }

    public void save(Path path, Json json) {
        try {
            ioService.write(path, json.write());
        } catch (Exception e) {
            throw new JsonException(String.format("Could not write json file %s", path), e);
        }
    }

    public boolean delete(Path path) {
        try {
            return ioService.delete(path);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not delete json file %s", path), e);
        }
    }
}
