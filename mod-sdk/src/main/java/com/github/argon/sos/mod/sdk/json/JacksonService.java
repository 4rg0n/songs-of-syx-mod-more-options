package com.github.argon.sos.mod.sdk.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses Jackson as JSON parser and writer.
 */
@RequiredArgsConstructor
public class JacksonService implements JsonService {

    private final ObjectMapper objectMapper;
    private final PrettyPrinter prettyPrinter;
    private final IOService ioService;

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        return load(path)
            .map(json -> {
                try {
                    return objectMapper.readValue(json, clazz);
                } catch (JsonProcessingException e) {
                    throw new JsonException(String.format("Could not map json from %s for %s", path, clazz.getSimpleName()), e);
                }
            });
    }

    public Optional<String> load(Path path) {
        try {
            return Optional.ofNullable(ioService.read(path));
        } catch (IOException e) {
            throw new JsonException(String.format("Could not rad json file %s", path), e);
        }
    }

    public void save(Path path, Object object) {
        String jsonString;
        try {
            jsonString = objectMapper
                .writer(prettyPrinter)
                .writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not create json from object %s for saving in %s",
                path, object.getClass().getSimpleName()), e);
        }

        save(path, jsonString);
    }

    public void save(Path path, String json) {
        try {
            ioService.write(path, json);
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
